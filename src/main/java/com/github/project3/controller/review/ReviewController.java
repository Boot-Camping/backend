package com.github.project3.controller.review;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.project3.dto.review.ReviewRequest;
import com.github.project3.dto.review.ReviewResponse;
import com.github.project3.dto.review.ReviewSummaryResponse;
import com.github.project3.service.review.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
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
     * 사용자가 특정 캠프에 대한 리뷰를 작성합니다.
     *
     * @param campId            캠프의 고유 ID
     * @param userId            사용자의 고유 ID
     * @param reviewRequestJson 리뷰 작성 요청 정보가 담긴 JSON 문자열
     * @param reviewImages      리뷰와 함께 업로드할 이미지 리스트 (선택사항)
     * @return 작성된 리뷰에 대한 응답 정보를 포함한 ResponseEntity 객체
     */
    @Operation(summary = "리뷰 작성", description = "사용자가 특정 캠프에 대한 리뷰를 작성합니다.")
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

    /**
     * 모든 리뷰를 조회합니다.
     *
     * @return 모든 리뷰 목록을 포함한 ResponseEntity 객체
     */
    @Operation(summary = "모든 리뷰 조회", description = "모든 리뷰를 조회합니다.")
    @GetMapping("/all")
    public ResponseEntity<List<ReviewSummaryResponse>> getAllReviews() {
        List<ReviewSummaryResponse> reviews = reviewService.getAllReviews();
        return ResponseEntity.status(200).body(reviews);
    }

    /**
     * 특정 캠프에 대한 리뷰를 조회합니다.
     *
     * @param campId  캠프의 고유 ID
     * @return 해당 캠프에 대한 리뷰 목록을 포함한 ResponseEntity 객체
     */
    @Operation(summary = "캠프별 리뷰 조회", description = "특정 캠프에 대한 리뷰를 조회합니다.")
    @GetMapping("/camp/{campId}")
    public ResponseEntity<List<ReviewResponse>> getReviewsByCampId(@PathVariable Integer campId) {
        List<ReviewResponse> reviews = reviewService.getReviewsByCampId(campId);
        return ResponseEntity.status(200).body(reviews);
    }

    /**
     * 특정 사용자의 리뷰를 조회합니다.
     *
     * @param userId  사용자의 고유 ID
     * @return 해당 사용자의 리뷰 목록을 포함한 ResponseEntity 객체
     */
    @Operation(summary = "사용자별 리뷰 조회", description = "특정 사용자의 리뷰를 조회합니다.")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ReviewSummaryResponse>> getReviewsByUserId(@PathVariable Integer userId) {
        List<ReviewSummaryResponse> reviews = reviewService.getReviewsByUserId(userId);
        return ResponseEntity.status(200).body(reviews);
    }

    /**
     * 사용자가 작성한 리뷰를 수정합니다.
     *
     * @param reviewId          수정하려는 리뷰의 고유 ID
     * @param userId            사용자의 고유 ID
     * @param accessKey         수정 권한을 확인하기 위한 액세스 키
     * @param reviewRequestJson 리뷰 수정 요청 정보가 담긴 JSON 문자열
     * @param reviewImages      수정된 리뷰와 함께 업로드할 이미지 리스트 (선택사항)
     * @return 수정된 리뷰에 대한 응답 정보를 포함한 ResponseEntity 객체
     */
    @Operation(summary = "리뷰 수정", description = "사용자가 작성한 리뷰를 수정합니다.")
    @PutMapping("/{reviewId}/{userId}")
    public ResponseEntity<ReviewResponse> updateReview(
            @PathVariable Integer reviewId,
            @PathVariable Integer userId,
            @RequestHeader("Authorization") String accessKey,
            @RequestPart("reviewRequest") String reviewRequestJson,
            @RequestPart(value = "reviewImages", required = false) List<MultipartFile> reviewImages) throws IOException {

        // JSON 문자열을 ReviewRequest 객체로 변환
        ReviewRequest reviewRequest = objectMapper.readValue(reviewRequestJson, ReviewRequest.class);

        // 리뷰 수정하고 결과를 ReviewResponse로 반환
        ReviewResponse reviewResponse = reviewService.updateReview(userId, reviewId, accessKey, reviewRequest, reviewImages);

        // HTTP 상태 200(ok)와 함께 수정된 리뷰 응답을 반환
        return ResponseEntity.status(200).body(reviewResponse);
    }

    /**
     * 사용자가 작성한 리뷰를 삭제합니다.
     *
     * @param accessKey  삭제 권한을 확인하기 위한 액세스 키
     * @param reviewId   삭제하려는 리뷰의 고유 ID
     * @param userId     사용자의 고유 ID
     * @return 성공적으로 삭제된 경우 HTTP 상태 204(no content)를 반환
     */
    @Operation(summary = "리뷰 삭제", description = "사용자가 작성한 리뷰를 삭제합니다.")
    @DeleteMapping("/{reviewId}/{userId}")
    public ResponseEntity<Void> deleteReview(
            @RequestHeader("Authorization") String accessKey,
            @PathVariable Integer reviewId,
            @PathVariable Integer userId) {
        // 리뷰 삭제
        reviewService.deleteReview(userId, reviewId, accessKey);

        // 성공적으로 삭제된 경우 204 반환
        return ResponseEntity.noContent().build();
    }
}
