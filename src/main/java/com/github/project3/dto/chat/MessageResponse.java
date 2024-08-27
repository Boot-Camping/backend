package com.github.project3.dto.chat;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class MessageResponse {
    private Integer id;
    private Integer chatRoomId;
    private Integer senderId;
    private String senderLoginId;
    private String content;
    private LocalDateTime sentAt;

    // 스태틱 팩토리 메소드
    public static MessageResponse of(Integer id, Integer chatRoomId, Integer senderId, String senderLoginId, String content, LocalDateTime sentAt) {
        return MessageResponse.builder()
                .id(id)
                .chatRoomId(chatRoomId)
                .senderId(senderId)
                .senderLoginId(senderLoginId)
                .content(content)
                .sentAt(sentAt)
                .build();
    }
}