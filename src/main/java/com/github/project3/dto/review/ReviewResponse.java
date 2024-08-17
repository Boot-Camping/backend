package com.github.project3.dto.review;

import com.github.project3.entity.review.enums.Tag;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
public class ReviewResponse {
    private Integer id;
    private String loginId;
    private String campName;
    private Integer grade;
    private String reviewContent;
    private LocalDateTime createdAt;
    private List<Tag> reviewTags;
    private List<String> reviewImages;
    private long reviewCount;

    // 정적 팩토리 메서드로 객체 생성
    public static ReviewResponse of(Integer id, String loginId, String campName, Integer grade, String reviewContent, LocalDateTime createdAt, List<Tag> reviewTags, List<String> reviewImages, long reviewCount) {
        return ReviewResponse.builder()
                .id(id)
                .loginId(loginId)
                .campName(campName)
                .grade(grade)
                .reviewContent(reviewContent)
                .createdAt(createdAt)
                .reviewTags(reviewTags)
                .reviewImages(reviewImages)
                .reviewCount(reviewCount)
                .build();
    }
}
