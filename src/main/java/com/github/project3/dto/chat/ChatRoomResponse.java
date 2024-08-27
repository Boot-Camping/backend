package com.github.project3.dto.chat;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@Data
public class ChatRoomResponse {
    private Integer id;
    private String name;
    private String createdBy;
    private String joinedBy;
    private LocalDateTime createdAt;

    // 스태틱 팩토리 메소드
    public static ChatRoomResponse of(Integer id, String name, String createdBy, String joinedBy, LocalDateTime createdAt) {
        return ChatRoomResponse.builder()
                .id(id)
                .name(name)
                .createdBy(createdBy)
                .joinedBy(joinedBy)
                .createdAt(createdAt)
                .build();
    }
}
