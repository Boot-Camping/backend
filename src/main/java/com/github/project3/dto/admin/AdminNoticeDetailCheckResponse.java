package com.github.project3.dto.admin;

import com.github.project3.entity.notice.NoticeEntity;
import com.github.project3.entity.notice.NoticeImageEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminNoticeDetailCheckResponse {
    private Integer id;
    private String title;
    private String description;
    private List<String> imageUrl;
    private LocalDateTime createAt;

    public static AdminNoticeDetailCheckResponse from(NoticeEntity notice){
        AdminNoticeDetailCheckResponse response = new AdminNoticeDetailCheckResponse();
        response.id = notice.getId();
        response.title = notice.getTitle();
        response.description = notice.getDescription();
        response.imageUrl = notice.getImages().stream().map(NoticeImageEntity::getImageUrl).collect(Collectors.toList());
        response.createAt = notice.getCreatedAt();

        return response;
    }
}
