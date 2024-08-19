package com.github.project3.controller.review;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.project3.dto.review.ReviewRequest;
import com.github.project3.dto.review.ReviewResponse;
import com.github.project3.dto.review.ReviewSummaryResponse;
import com.github.project3.service.review.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/review")
public class ReviewController {
    // ReviewService를 주입받기 위한 필드

    private final ReviewService reviewService;
    private final ObjectMapper objectMapper; // JSON 변환을 위한 ObjectMapper

    /**
     * 사용자가 특정 캠프에 대한 리뷰를 작성하는 API 엔드포인트입니다.
     *
     * @param campId        캠프의 고유 ID
     * @param userId        사용자의 고유 ID
     * @param reviewRequestJson 리뷰 작성 요청 정보가 담긴 DTO 객체
     * @return 작성된 리뷰에 대한 응답 정보를 포함한 ResponseEntity 객체
     */
    @PostMapping("/{campId}/{userId}")
    public ResponseEntity<ReviewResponse> createReview(
            @PathVariable Integer campId,
            @PathVariable Integer userId,
            // 요청 본문에서 리뷰 정보가 담긴 DTO 추출
            @RequestPart("reviewRequest") String reviewRequestJson,
            @RequestPart(value = "reviewImages", required = false) List<MultipartFile> reviewImages) throws IOException {

        // JSON 문자열을 ReviewRequest 객체로 변환
        ReviewRequest reviewRequest = objectMapper.readValue(reviewRequestJson, ReviewRequest.class);

        // 리뷰 생성하고 결과를 ReviewResponse로 반환
        ReviewResponse reviewResponse = reviewService.createReview(userId, campId, reviewRequest, reviewImages);

        // HTTP 상태 201(created)과 함께 생성된 리뷰 응답을 반환
        return ResponseEntity.status(201).body(reviewResponse);
    }

    // 모든 리뷰 조회
    @GetMapping("/all")
    public ResponseEntity<List<ReviewSummaryResponse>> getAllReviews() {
        List<ReviewSummaryResponse> reviews = reviewService.getAllReviews();
        return ResponseEntity.status(200).body(reviews);
    }

    // 캠프별 리뷰 조회
    @GetMapping("/camp/{campId}")
    public ResponseEntity<List<ReviewResponse>> getReviewsByCampId(@PathVariable Integer campId) {
        List<ReviewResponse> reviews = reviewService.getReviewsByCampId(campId);
        return ResponseEntity.status(200).body(reviews);
    }

    // 유저별 리뷰 조회
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ReviewSummaryResponse>> getReviewsByUserId(@PathVariable Integer userId) {
        List<ReviewSummaryResponse> reviews = reviewService.getReviewsByUserId(userId);
        return ResponseEntity.status(200).body(reviews);
    }
}
