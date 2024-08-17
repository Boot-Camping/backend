package com.github.project3.dto.mypage;

import com.github.project3.entity.notice.NoticeEntity;
import com.github.project3.entity.user.UserImageEntity;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class NoticeResponse {
    private Integer id;
    private String title;
    private String createAt;

    // 스태틱 팩토리 메서드
    public static NoticeResponse from(NoticeEntity notice) {
        NoticeResponse response = new NoticeResponse();
        response.id = notice.getId();
        response.title = notice.getTitle();
        response.createAt = notice.getCreatedAt().toString();

        return response;
    }
}
