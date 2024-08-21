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
    private Integer id;
    private String title;
    private LocalDateTime createAt;

    // 스태틱 팩토리 메서드
    public static AdminNoticeCheckResponse from(NoticeEntity notice){
        AdminNoticeCheckResponse response = new AdminNoticeCheckResponse();
        response.id = notice.getId();
        response.title = notice.getTitle();
        response.createAt = notice.getCreatedAt();

        return response;
    }
}
