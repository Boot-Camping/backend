package com.github.project3.dto.admin;

import com.github.project3.entity.notice.NoticeEntity;
import lombok.Data;

import java.io.Serializable;

@Data
public class AdminNoticeRegisterResponse {
    private Integer id;
    private String title;
    private String createAt;

    // 스태틱 팩토리 메서드
    public static AdminNoticeRegisterResponse from(NoticeEntity notice) {
        AdminNoticeRegisterResponse response = new AdminNoticeRegisterResponse();
        response.id = notice.getId();
        response.title = notice.getTitle();
        response.createAt = notice.getCreatedAt().toString();

        return response;
    }
}
