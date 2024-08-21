package com.github.project3.dto.reply;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class ReplyResponse {
    private Integer id;
    private String userLoginId;
    private String comment;
    private LocalDateTime createdAt;

    public static ReplyResponse of(Integer id, String userLoginId, String comment, LocalDateTime createdAt) {
        return ReplyResponse.builder()
                .id(id)
                .userLoginId(userLoginId)
                .comment(comment)
                .createdAt(createdAt)
                .build();
    }
}
