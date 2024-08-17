package com.github.project3.dto.review;

import com.github.project3.entity.review.enums.Tag;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Getter
public class ReviewRequest {
    private Integer userId;
    private Integer campId;
    private String content;
    private Integer grade;
    private List<Tag> tags;
    private List<String> imageUrls;

    // 정적 팩토리 메서드로 객체 생성
    public static ReviewRequest of(Integer userId, Integer campId, String content, Integer grade, List<Tag> tags, List<String> imageUrls) {
        return ReviewRequest.builder()
                .userId(userId)
                .campId(campId)
                .content(content)
                .grade(grade)
                .tags(tags)
                .imageUrls(imageUrls)
                .build();
    }
}
