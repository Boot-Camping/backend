package com.github.project3.service.review;

import com.github.project3.dto.review.ReviewRequest;
import com.github.project3.dto.review.ReviewResponse;
import com.github.project3.dto.review.ReviewSummaryResponse;
import com.github.project3.entity.camp.CampEntity;
import com.github.project3.entity.review.ReviewEntity;
import com.github.project3.entity.review.ReviewImageEntity;
import com.github.project3.entity.review.ReviewTagEntity;
import com.github.project3.entity.review.enums.Tag;
import com.github.project3.entity.user.UserEntity;
import com.github.project3.entity.user.enums.TransactionType;
import com.github.project3.repository.camp.CampRepository;
import com.github.project3.repository.review.ReviewRepository;
import com.github.project3.repository.user.UserRepository;
import com.github.project3.service.S3Service;
import com.github.project3.service.cash.CashService;
import com.github.project3.service.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final CampRepository campRepository;
    private final S3Service s3Service;
    private final CashService cashService;

    /**
     * 새로운 리뷰를 생성합니다.
     *
     * @param userId        리뷰를 작성한 사용자의 ID
     * @param campId        리뷰가 작성된 캠핑장의 ID
     * @param reviewRequest 리뷰 작성 요청 정보를 담고 있는 DTO 객체
     * @param reviewImages  리뷰와 함께 업로드된 이미지 파일 리스트
     */
    @Transactional
    public void createReview(Integer userId, Integer campId, ReviewRequest reviewRequest, List<MultipartFile> reviewImages) {
        // 사용자와 캠핑장 정보 가져오기
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("유저 정보가 없습니다."));
        CampEntity camp = campRepository.findById(campId)
                .orElseThrow(() -> new NotFoundException("캠핑장 정보가 없습니다."));

        // ReviewRequest DTO에 campId와 userId를 직접 전달하여 객체 생성
        ReviewRequest updatedRequest = ReviewRequest.of(
                userId,
                campId,
                reviewRequest.getContent(),
                reviewRequest.getGrade(),
                reviewRequest.getTags(),
                reviewRequest.getImageUrls()
        );

        // 새로운 리뷰 엔티티 생성 및 초기화
        ReviewEntity review = ReviewEntity.builder()
                .user(user)
                .camp(camp)
                .content(reviewRequest.getContent())
                .grade(reviewRequest.getGrade())
                .build();

        // 태그 처리: null 또는 빈 리스트일 경우 무시
        if (updatedRequest.getTags() != null && !updatedRequest.getTags().isEmpty()) {
            List<ReviewTagEntity> reviewTags = updatedRequest.getTags().stream()
                    .map(tag -> new ReviewTagEntity(review, tag))
                    .collect(Collectors.toList());
            review.getTags().addAll(reviewTags);
        }

        if (reviewImages != null && !reviewImages.isEmpty()) {
            for (MultipartFile image : reviewImages) {
                try {
                    String imageUrl = s3Service.uploadReviewImage(image);
                    ReviewImageEntity reviewImageEntity = new ReviewImageEntity(review, imageUrl);
                    review.getImages().add(reviewImageEntity);
                } catch (IOException e) {
                    // 로깅 또는 적절한 오류 처리
                    System.err.println("Image upload failed: " + e.getMessage());
                    throw new RuntimeException("Image upload failed", e);
                }
            }
        }

        // 리뷰엔티티를 데이터베이스에 저장
        reviewRepository.save(review);

        // 리뷰 작성 시 500원 적립
        cashService.processTransaction(user, 500, TransactionType.REWARD);

        // 해당 캠핑장 리뷰 개수 계산
        long reviewCount = reviewRepository.countByCampId(camp.getId());
    }

    /**
     * 모든 리뷰를 조회합니다.
     *
     * @return 모든 리뷰의 요약 정보를 담고 있는 ReviewSummaryResponse 리스트
     */
    @Transactional(readOnly = true)
    public List<ReviewSummaryResponse> getAllReviews(){
        return reviewRepository.findAll().stream()
                .map(review -> ReviewSummaryResponse.of(
                        review.getId(),
                        review.getUser().getLoginId(),
                        review.getCamp().getName(),
                        review.getContent(),
                        review.getImages().isEmpty() ? null : review.getImages().get(0).getImageUrl(),
                        review.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }

    /**
     * 특정 캠핑장에 대한 리뷰들을 조회합니다.
     *
     * @param campId  조회할 캠핑장의 ID
     * @return 해당 캠핑장에 작성된 리뷰의 상세 정보를 담고 있는 ReviewResponse 리스트
     */
    @Transactional(readOnly = true)
    public List<ReviewResponse> getReviewsByCampId(Integer campId) {
        List<ReviewEntity> reviews = reviewRepository.findByCampId(campId);
        long reviewCount = reviewRepository.countByCampId(campId);
        return reviews.stream()
                .map(review -> ReviewResponse.from(
                        review,
                        review.getUser().getLoginId(),
                        review.getCamp().getName(),
                        reviewCount
                ))
                .collect(Collectors.toList());
    }

    /**
     * 특정 사용자가 작성한 리뷰들을 조회합니다.
     *
     * @param userId  조회할 사용자의 ID
     * @return 해당 사용자가 작성한 리뷰의 요약 정보를 담고 있는 ReviewSummaryResponse 리스트
     */
    @Transactional(readOnly = true)
    public List<ReviewResponse> getReviewsByUserId(Integer userId){
        List<ReviewEntity> reviews = reviewRepository.findByUserId(userId); // 태그와 이미지를 포함한 리뷰 조회
        return reviews.stream()
                .map(review -> ReviewResponse.from(
                        review,
                        review.getUser().getLoginId(),
                        review.getCamp().getName(),
                        reviews.size()
                ))
                .collect(Collectors.toList());
    }

    /**
     * 기존 리뷰를 수정합니다.
     *
     * @param userId          리뷰를 작성한 사용자의 ID
     * @param reviewId        수정할 리뷰의 ID
     * @param access          수정 권한을 확인하기 위한 액세스 키
     * @param reviewRequest   수정할 리뷰의 내용을 담고 있는 DTO 객체
     * @param newReviewImages 수정할 리뷰에 새롭게 추가할 이미지 파일 리스트
     */
    @Transactional
    public void updateReview(Integer userId, Integer reviewId, String access, ReviewRequest reviewRequest, List<MultipartFile> newReviewImages){
        // 리뷰 엔티티 가져오기
        ReviewEntity existingReview = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NotFoundException("리뷰를 찾을 수 없습니다."));

        // 기존 리뷰 데이터를 기반으로 새로운 리뷰 엔티티 생성
        ReviewEntity updatedReview = ReviewEntity.builder()
                .id(existingReview.getId())
                .user(existingReview.getUser())
                .camp(existingReview.getCamp())
                .content(reviewRequest.getContent() != null ? reviewRequest.getContent() : existingReview.getContent())
                .grade(reviewRequest.getGrade() != null ? reviewRequest.getGrade() : existingReview.getGrade())
                .createdAt(existingReview.getCreatedAt())
                .tags(existingReview.getTags())
                .images(new ArrayList<>()) // 이미지 리스트 초기화
                .build();

        // 태그 업데이트: 기존 태그 유지, 새로운 태그가 있을 경우에만 추가,삭제
        if (reviewRequest.getTags() != null) {
            List<ReviewTagEntity> existingTags = updatedReview.getTags();

            // 기존 태그 삭제
            existingTags.removeIf(tagEntity -> !reviewRequest.getTags().contains(tagEntity.getTag()));

            // 새로운 태그 추가
            for (Tag tag : reviewRequest.getTags()) {
                if (existingTags.stream().noneMatch(tagEntity -> tagEntity.getTag().equals(tag))) {
                    updatedReview.getTags().add(new ReviewTagEntity(updatedReview, tag));
                }
            }
        }

        // 기존 이미지 처리: 삭제되지 않은 이미지만 유지
        if (reviewRequest.getImageUrls() != null) {
            List<String> remainingImageUrls = reviewRequest.getImageUrls();
            for (ReviewImageEntity imageEntity : existingReview.getImages()) {
                if (remainingImageUrls.contains(imageEntity.getImageUrl())) {
                    updatedReview.getImages().add(imageEntity); // 삭제되지 않은 이미지만 추가
                }
            }
        }

        // 새로운 이미지 추가
        if (newReviewImages != null && !newReviewImages.isEmpty()) {
            for (MultipartFile image : newReviewImages) {
                try {
                    String imageUrl = s3Service.uploadReviewImage(image);
                    updatedReview.getImages().add(new ReviewImageEntity(updatedReview, imageUrl));
                } catch (IOException e) {
                    throw new RuntimeException("Image upload failed", e);
                }
            }
        }

        // 리뷰 엔티티를 데이터베이스에 저장
        reviewRepository.save(updatedReview);

        // 해당 캠핑장 리뷰 개수 계산
        long reviewCount = reviewRepository.countByCampId(updatedReview.getCamp().getId());
    }

    /**
     * 기존 리뷰를 삭제합니다.
     *
     * @param userId   리뷰를 작성한 사용자의 ID
     * @param reviewId 삭제할 리뷰의 ID
     * @param access   삭제 권한을 확인하기 위한 액세스 키
     */
    @Transactional
    public void deleteReview(Integer userId, Integer reviewId, String access) {
        // 리뷰 엔티티 가져오기
        ReviewEntity review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NotFoundException("리뷰를 찾을 수 없습니다."));

//        // 리뷰에 연결된 이미지 삭제 (S3 관련 구현해야함)
//        for (ReviewImageEntity image : review.getImages()) {
//            s3Service.deleteReviewImage(image.getImageUrl());
//        }

        // 리뷰 엔티티 삭제
        reviewRepository.delete(review);
    }
}
