package com.github.project3.controller.review;

import com.github.project3.dto.review.ReviewRequest;
import com.github.project3.dto.review.ReviewResponse;
import com.github.project3.service.review.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/review")
public class ReviewController {
    // ReviewService를 주입받기 위한 필드

    private final ReviewService reviewService;

    /**
     * 사용자가 특정 캠프에 대한 리뷰를 작성하는 API 엔드포인트입니다.
     *
     * @param campId        캠프의 고유 ID
     * @param userId        사용자의 고유 ID
     * @param reviewRequest 리뷰 작성 요청 정보가 담긴 DTO 객체
     * @return 작성된 리뷰에 대한 응답 정보를 포함한 ResponseEntity 객체
     */
    @PostMapping("/{campId}/{userId}")
    public ResponseEntity<ReviewResponse> createReview(
            @PathVariable Integer campId,
            @PathVariable Integer userId,
            // 요청 본문에서 리뷰 정보가 담긴 DTO 추출
            @RequestBody ReviewRequest reviewRequest) {

        // 리뷰 생성하고 결과를 ReviewResponse로 반환
        ReviewResponse reviewResponse = reviewService.createReview(userId, campId, reviewRequest);

        // HTTP tkdxo 201(created)과 함께 생성된 리뷰 응답을 반환
        return ResponseEntity.status(201).body(reviewResponse);
    }
}
