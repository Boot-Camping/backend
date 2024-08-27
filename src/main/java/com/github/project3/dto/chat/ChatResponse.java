package com.github.project3.dto.chat;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChatResponse {
    private Integer id;
    private String name;
    private LocalDateTime createdAt;
}
