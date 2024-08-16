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
    private final ReviewService reviewService;

    @PostMapping("/{campId}/{userId}")
    public ResponseEntity<ReviewResponse> createReview(
            @PathVariable Integer campId,
            @PathVariable Integer userId,
            @RequestBody ReviewRequest reviewRequest) {

        // ReviewRequest DTO에 campId와 userId를 직접 전달하여 객체 생성
        ReviewRequest updatedRequest = ReviewRequest.of(
                userId, campId, reviewRequest.getContent(), reviewRequest.getGrade(), reviewRequest.getTags(), reviewRequest.getImageUrls()
        );

        // 리뷰 생성
        ReviewResponse reviewResponse = reviewService.createReview(updatedRequest);

        // 응답
        return ResponseEntity.status(201).body(reviewResponse);
    }
}
