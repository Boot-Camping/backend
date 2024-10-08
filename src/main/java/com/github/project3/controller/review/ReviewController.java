package com.github.project3.controller.review;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.project3.dto.review.ReviewRequest;
import com.github.project3.dto.review.ReviewResponse;
import com.github.project3.dto.review.ReviewSummaryResponse;
import com.github.project3.service.review.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;
    private final ObjectMapper objectMapper;

    /**
     * 리뷰 작성
     * @param campId 캠프의 고유 ID
     * @param userId 사용자의 고유 ID
     * @param reviewRequestJson 리뷰 작성 요청 정보가 담긴 JSON 문자열
     * @param reviewImages 리뷰와 함께 업로드할 이미지 리스트 (선택사항)
     * @return 작성 완료 메시지 응답
     */
    @Operation(summary = "리뷰 작성", description = "사용자가 특정 캠프에 대한 리뷰를 작성합니다.")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> createReview(
            @RequestParam Integer campId,
            @RequestParam Integer userId,
            @RequestPart("reviewRequest") String reviewRequestJson,
            @RequestPart(value = "reviewImages", required = false) List<MultipartFile> reviewImages) throws IOException {

        ReviewRequest reviewRequest = objectMapper.readValue(reviewRequestJson, ReviewRequest.class);
        reviewService.createReview(userId, campId, reviewRequest, reviewImages);
        return ResponseEntity.status(201).body("리뷰 작성이 성공적으로 완료되었습니다.");
    }

    /**
     * 모든 리뷰 조회
     * @return 모든 리뷰 목록을 포함한 ResponseEntity 객체
     */
    @Operation(summary = "모든 리뷰 조회", description = "모든 리뷰를 조회합니다.")
    @GetMapping
    public ResponseEntity<List<ReviewSummaryResponse>> getAllReviews() {
        List<ReviewSummaryResponse> reviews = reviewService.getAllReviews();
        return ResponseEntity.ok(reviews);
    }

    /**
     * 특정 캠프에 대한 리뷰 조회
     * @param campId 캠프의 고유 ID
     * @return 해당 캠프에 대한 리뷰 목록을 포함한 ResponseEntity 객체
     */
    @Operation(summary = "캠프별 리뷰 조회", description = "특정 캠프에 대한 리뷰를 조회합니다.")
    @GetMapping("/camp/{campId}")
    public ResponseEntity<List<ReviewResponse>> getReviewsByCampId(@PathVariable Integer campId) {
        List<ReviewResponse> reviews = reviewService.getReviewsByCampId(campId);
        return ResponseEntity.ok(reviews);
    }

    /**
     * 특정 사용자의 리뷰 조회
     * @param userId 사용자의 고유 ID
     * @return 해당 사용자의 리뷰 목록을 포함한 ResponseEntity 객체
     */
    @Operation(summary = "사용자별 리뷰 조회", description = "특정 사용자의 리뷰를 조회합니다.")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ReviewResponse>> getReviewsByUserId(@PathVariable Integer userId) {
        List<ReviewResponse> reviews = reviewService.getReviewsByUserId(userId);
        return ResponseEntity.ok(reviews);
    }

    /**
     * 리뷰 수정
     * @param reviewId 수정하려는 리뷰의 고유 ID
     * @param userId 사용자의 고유 ID
     * @param accessKey 수정 권한을 확인하기 위한 액세스 키
     * @param reviewRequestJson 리뷰 수정 요청 정보가 담긴 JSON 문자열
     * @param reviewImages 수정된 리뷰와 함께 업로드할 이미지 리스트 (선택사항)
     * @return 수정완료 메시지 응답
     */
    @Operation(summary = "리뷰 수정", description = "사용자가 작성한 리뷰를 수정합니다.")
    @PutMapping(value = "/{reviewId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> updateReview(
            @PathVariable Integer reviewId,
            @RequestParam Integer userId,
            @RequestHeader("Authorization") String accessKey,
            @RequestPart("reviewRequest") String reviewRequestJson,
            @RequestPart(value = "reviewImages", required = false) List<MultipartFile> reviewImages) throws IOException {

        ReviewRequest reviewRequest = objectMapper.readValue(reviewRequestJson, ReviewRequest.class);
        reviewService.updateReview(userId, reviewId, accessKey, reviewRequest, reviewImages);
        return ResponseEntity.ok("리뷰 수정이 성공적으로 완료되었습니다.");
    }

    /**
     * 리뷰 삭제
     * @param reviewId 삭제하려는 리뷰의 고유 ID
     * @param userId 사용자의 고유 ID
     * @param accessKey 삭제 권한을 확인하기 위한 액세스 키
     * @return 성공적으로 삭제된 경우 리뷰 삭제가 성공적으로 완료되었다는 메시지 반환
     */
    @Operation(summary = "리뷰 삭제", description = "사용자가 작성한 리뷰를 삭제합니다.")
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<String> deleteReview(
            @PathVariable Integer reviewId,
            @RequestParam Integer userId,
            @RequestHeader("Authorization") String accessKey) {

        reviewService.deleteReview(userId, reviewId, accessKey);
        return ResponseEntity.ok("리뷰 삭제가 성공적으로 완료되었습니다.");
    }
}
