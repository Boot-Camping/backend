package com.github.project3.dto.review;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class ReviewSummaryResponse {
    // 전체 리뷰 조회와 유저에 따른 리뷰조회에 필요한 정보만 요약한 응답 만들기
    private Integer id;
    private String userLoginId;
    private String campName;
    private String content;
    private String reviewImage;
    private LocalDateTime createdAt;

    public static ReviewSummaryResponse of(Integer id, String userLoginId, String campName, String content, String reviewImage, LocalDateTime createdAt) {
        return ReviewSummaryResponse.builder()
                .id(id)
                .userLoginId(userLoginId)
                .campName(campName)
                .content(content)
                .reviewImage(reviewImage)
                .createdAt(createdAt)
                .build();
    }
}
