package com.github.project3.dto.mypage;

import com.github.project3.entity.notice.NoticeEntity;
import com.github.project3.entity.notice.NoticeImageEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NoticeDetailResponse {
    private Integer id;
    private String title;
    private String description;
    private List<String> images;
    private LocalDateTime createAt;

    public static NoticeDetailResponse from(NoticeEntity notice){
        NoticeDetailResponse response = new NoticeDetailResponse();
        response.id = notice.getId();
        response.title = notice.getTitle();
        response.description = notice.getDescription();
        response.images = notice.getImages().stream().map(NoticeImageEntity::getImageUrl).collect(Collectors.toList());
        response.createAt = notice.getCreatedAt();

        return response;
    }
}
