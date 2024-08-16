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
import com.github.project3.service.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final CampRepository campRepository;
    private final CashRepository cashRepository;

    @Transactional
    public ReviewResponse createReview(ReviewRequest reviewRequest) {
        // 사용자와 캠핑장 정보 가져오기
        UserEntity user = userRepository.findById(reviewRequest.getUserId())
                .orElseThrow(() -> new NotFoundException("유저 정보가 없습니다."));
        CampEntity camp = campRepository.findById(reviewRequest.getCampId())
                .orElseThrow(() -> new NotFoundException("캠핑장 정보가 없습니다."));

        // 리뷰 엔티티 생성
        ReviewEntity review = ReviewEntity.builder()
                .user(user)
                .camp(camp)
                .content(reviewRequest.getContent())
                .grade(reviewRequest.getGrade())
                .build();

        // 태그와 이미지 처리
        List<ReviewTagEntity> reviewTags = reviewRequest.getTags().stream()
                .map(tag -> new ReviewTagEntity(review, tag))
                .collect(Collectors.toList());
        List<ReviewImageEntity> reviewImages = reviewRequest.getImageUrls().stream()
                .map(url -> new ReviewImageEntity(review, url))
                .collect(Collectors.toList());

        review.getTags().addAll(reviewTags);
        review.getImages().addAll(reviewImages);

        // 리뷰 저장
        reviewRepository.save(review);

        // 리뷰 작성 후 500원 적립
        int rewardAmount = 500;
        int newBalance = user.getCash().stream()
                .mapToInt(CashEntity::getBalanceAfterTransaction)
                .sum() + rewardAmount;

        CashEntity cashTransaction = CashEntity.of(user, rewardAmount, TransactionType.DEPOSIT, newBalance);
        cashRepository.save(cashTransaction);


        // 해당 캠핑장 리뷰 개수 계산
        long reviewCount = reviewRepository.countByCampId(camp.getId());

        // 응답 DTO 생성
        return ReviewResponse.of(
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
