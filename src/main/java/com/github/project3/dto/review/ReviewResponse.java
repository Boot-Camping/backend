package com.github.project3.dto.review;

import com.github.project3.entity.review.ReviewEntity;
import com.github.project3.entity.review.enums.Tag;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Builder
@Getter
public class ReviewResponse {
    private Integer id;
    private Integer campId;
    private String loginId;
    private String campName;
    private Integer grade;
    private String reviewContent;
    private LocalDateTime createdAt;
    private List<Tag> reviewTags;
    private List<String> reviewImages;
    private long reviewCount;

    // 정적 팩토리 메서드로 객체 생성
    public static ReviewResponse from(ReviewEntity reviewEntity, String loginId, String campName, long reviewCount) {
        return ReviewResponse.builder()
                .id(reviewEntity.getId())
                .campId(reviewEntity.getCamp().getId())
                .loginId(loginId)
                .campName(campName)
                .grade(reviewEntity.getGrade())
                .reviewContent(reviewEntity.getContent())
                .createdAt(reviewEntity.getCreatedAt())
                .reviewTags(reviewEntity.getTags().stream().map(tag -> tag.getTag()).collect(Collectors.toList()))
                .reviewImages(reviewEntity.getImages().stream().map(image -> image.getImageUrl()).collect(Collectors.toList()))
                .reviewCount(reviewCount)
                .build();
    }
}
