package com.github.project3.dto.chat;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@Data
public class MessageRequest {
    private Integer chatRoomId;
    private Integer senderId;
    private String content;
}