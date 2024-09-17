package com.github.project3.dto.review;

import com.github.project3.entity.review.ReviewEntity;
import com.github.project3.entity.review.ReviewImageEntity;
import com.github.project3.entity.review.ReviewTagEntity;
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

    public ReviewResponse(Integer id, Integer campId, String loginId, String campName, Integer grade, String reviewContent,
                          LocalDateTime createdAt, List<Tag> reviewTags, List<String> reviewImages, long reviewCount) {
        this.id = id;
        this.campId = campId;
        this.loginId = loginId;
        this.campName = campName;
        this.grade = grade;
        this.reviewContent = reviewContent;
        this.createdAt = createdAt;
        this.reviewTags = reviewTags;
        this.reviewImages = reviewImages;
        this.reviewCount = reviewCount;
    }

    public static ReviewResponse from(ReviewEntity reviewEntity, String loginId, String campName, long reviewCount) {
        List<Tag> tags = reviewEntity.getTags().stream()
                .map(ReviewTagEntity::getTag)
                .collect(Collectors.toList());

        List<String> imageUrls = reviewEntity.getImages().stream()
                .map(ReviewImageEntity::getImageUrl)
                .collect(Collectors.toList());

        return new ReviewResponse(
                reviewEntity.getId(),
                reviewEntity.getCamp().getId(),
                loginId,
                campName,
                reviewEntity.getGrade(),
                reviewEntity.getContent(),
                reviewEntity.getCreatedAt(),
                tags,
                imageUrls,
                reviewCount
        );
    }
}
