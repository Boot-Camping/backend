package com.github.project3.dto.chat;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MessageResponse {
    private Integer id;
    private Integer chatRoomId;
    private Integer senderId;
    private String senderLoginId;
    private String content;
    private LocalDateTime sentAt;
}