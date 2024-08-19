package com.github.project3.dto.admin;

import com.github.project3.entity.notice.NoticeEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminNoticeCheckResponse {
    private String title;
    private LocalDateTime createAt;

    public static AdminNoticeCheckResponse from(NoticeEntity notice){
        AdminNoticeCheckResponse response = new AdminNoticeCheckResponse();
        response.title = notice.getTitle();
        response.createAt = notice.getCreatedAt();

        return response;
    }
}
