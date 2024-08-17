package com.github.project3.service.review;

import com.github.project3.dto.review.ReviewRequest;
import com.github.project3.dto.review.ReviewResponse;
import com.github.project3.entity.camp.CampEntity;
import com.github.project3.entity.review.ReviewEntity;
import com.github.project3.entity.review.ReviewImageEntity;
import com.github.project3.entity.review.ReviewTagEntity;
import com.github.project3.entity.user.CashEntity;
import com.github.project3.entity.user.UserEntity;
import com.github.project3.entity.user.enums.TransactionType;
import com.github.project3.repository.camp.CampRepository;
import com.github.project3.repository.cash.CashRepository;
import com.github.project3.repository.review.ReviewRepository;
import com.github.project3.repository.user.UserRepository;
import com.github.project3.service.S3Service;
import com.github.project3.service.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
}
