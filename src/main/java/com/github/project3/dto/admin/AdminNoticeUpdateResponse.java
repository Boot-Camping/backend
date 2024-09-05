package com.github.project3.dto.admin;

import com.github.project3.entity.notice.NoticeEntity;
import com.github.project3.entity.notice.NoticeImageEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminNoticeUpdateResponse {
    private String title;
    private String description;
    private List<String> imageUrl;

    public static AdminNoticeUpdateResponse from(NoticeEntity notice){
        AdminNoticeUpdateResponse response = new AdminNoticeUpdateResponse();
        response.title = notice.getTitle();
        response.description = notice.getDescription();
        response.imageUrl = notice.getImages().stream().map(NoticeImageEntity::getImageUrl).collect(Collectors.toList());

        return response;
    }
}
