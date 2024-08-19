package com.github.project3.service.review;

import com.github.project3.dto.review.ReviewRequest;
import com.github.project3.dto.review.ReviewResponse;
import com.github.project3.dto.review.ReviewSummaryResponse;
import com.github.project3.entity.camp.CampEntity;
import com.github.project3.entity.review.ReviewEntity;
import com.github.project3.entity.review.ReviewImageEntity;
import com.github.project3.entity.review.ReviewTagEntity;
import com.github.project3.entity.review.enums.Tag;
import com.github.project3.entity.user.CashEntity;
import com.github.project3.entity.user.UserEntity;
import com.github.project3.entity.user.enums.TransactionType;
import com.github.project3.jwt.JwtTokenProvider;
import com.github.project3.repository.camp.CampRepository;
import com.github.project3.repository.cash.CashRepository;
import com.github.project3.repository.review.ReviewRepository;
import com.github.project3.repository.user.UserRepository;
import com.github.project3.service.S3Service;
import com.github.project3.service.exceptions.NotFoundException;
import com.github.project3.service.exceptions.UnauthorizedException;
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
    private final CashRepository cashRepository;
    private final S3Service s3Service;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public ReviewResponse createReview(Integer userId, Integer campId, ReviewRequest reviewRequest, List<MultipartFile> reviewImages) {
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

        // 리뷰 작성 후 500원 적립
        int rewardAmount = 500;
        int currentBalance = user.getCash().stream()
                .max((c1,c2) -> c1.getTransactionDate().compareTo(c2.getTransactionDate()))
                .map(CashEntity::getBalanceAfterTransaction)
                .orElse(0);
        int newBalance = currentBalance + rewardAmount;

        // 적립 거래 내역 생성 및 저장
        CashEntity cashTransaction = CashEntity.of(user, rewardAmount, TransactionType.REWARD, newBalance);
        cashRepository.save(cashTransaction);

        // 해당 캠핑장 리뷰 개수 계산
        long reviewCount = reviewRepository.countByCampId(camp.getId());

        // 응답 DTO 생성
        return ReviewResponse.of(
                review.getId(),
                user.getLoginId(),
                camp.getName(),
                review.getGrade(),
                review.getContent(),
                review.getCreatedAt(),
                review.getTags().stream().map(ReviewTagEntity::getTag).collect(Collectors.toList()),
                review.getImages().stream().map(ReviewImageEntity::getImageUrl).collect(Collectors.toList()),
                reviewCount
        );
    }

    // 모든 리뷰 조회
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

    // 캠프별 리뷰 조회
    @Transactional(readOnly = true)
    public List<ReviewResponse> getReviewsByCampId(Integer campId) {
        List<ReviewEntity> reviews = reviewRepository.findByCampId(campId);
        long reviewCount = reviewRepository.countByCampId(campId);
        return reviews.stream()
                .map(review -> ReviewResponse.of(
                        review.getId(),
                        review.getUser().getLoginId(),
                        review.getCamp().getName(),
                        review.getGrade(),
                        review.getContent(),
                        review.getCreatedAt(),
                        review.getTags().stream().map(tag -> tag.getTag()).collect(Collectors.toList()),
                        review.getImages().stream().map(image -> image.getImageUrl()).collect(Collectors.toList()),
                        reviewCount
                ))
                .collect(Collectors.toList());
    }

    // 유저별 리뷰 조회
    @Transactional(readOnly = true)
    public List<ReviewSummaryResponse> getReviewsByUserId(Integer userId){
        return reviewRepository.findByUserId(userId).stream()
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
    @Transactional
    public ReviewResponse updateReview(Integer userId, Integer reviewId, String access, ReviewRequest reviewRequest, List<MultipartFile> newReviewImages){
        // 리뷰 엔티티 가져오기
        ReviewEntity existingReview = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NotFoundException("리뷰를 찾을 수 없습니다."));

        // JWT에서 userId 추출
        Integer authenticatedUserId = jwtTokenProvider.getUserId(access);

        // 리뷰 작성 유저인지 확인
        if (!authenticatedUserId.equals(userId)) {
            throw new UnauthorizedException("리뷰를 수정할 권한이 없습니다.");
        }

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

        // 응답 DTO 생성
        return ReviewResponse.of(
                updatedReview.getId(),
                updatedReview.getUser().getLoginId(),
                updatedReview.getCamp().getName(),
                updatedReview.getGrade(),
                updatedReview.getContent(),
                updatedReview.getCreatedAt(),
                updatedReview.getTags().stream().map(ReviewTagEntity::getTag).collect(Collectors.toList()),
                updatedReview.getImages().stream().map(ReviewImageEntity::getImageUrl).collect(Collectors.toList()),
                reviewCount
        );
    }

    @Transactional
    public void deleteReview(Integer userId, Integer reviewId, String access) {
        // 리뷰 엔티티 가져오기
        ReviewEntity review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NotFoundException("리뷰를 찾을 수 없습니다."));

        // JWT에서 userId 추출
        Integer authenticatedUserId = jwtTokenProvider.getUserId(access);

        // 리뷰 작성 유저인지 확인
        if (!authenticatedUserId.equals(userId)) {
            throw new UnauthorizedException("리뷰를 삭제할 권한이 없습니다.");
        }

//        // 리뷰에 연결된 이미지 삭제 (S3 관련 구현해야함)
//        for (ReviewImageEntity image : review.getImages()) {
//            s3Service.deleteReviewImage(image.getImageUrl());
//        }

        // 리뷰 엔티티 삭제
        reviewRepository.delete(review);
    }
}
