package com.github.project3.dto.admin;

import com.github.project3.entity.notice.NoticeEntity;
import com.github.project3.entity.notice.NoticeImageEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminNoticeRegisterResponse {
    private Integer id;
    private String title;
    private List<String> imageUrl;
    private String createAt;

    // 스태틱 팩토리 메서드
    public static AdminNoticeRegisterResponse from(NoticeEntity notice) {
        AdminNoticeRegisterResponse response = new AdminNoticeRegisterResponse();
        response.id = notice.getId();
        response.title = notice.getTitle();
        response.createAt = notice.getCreatedAt().toString();
        if (notice.getImages() != null){
            response.imageUrl = notice.getImages().stream()
                    .map(NoticeImageEntity::getImageUrl).collect(Collectors.toList());
        } else {
            response.imageUrl = new ArrayList<>();
        }
        return response;
    }
}

