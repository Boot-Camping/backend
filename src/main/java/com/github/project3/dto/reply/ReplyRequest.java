package com.github.project3.dto.reply;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ReplyRequest {
    private Integer userId;
    private String comment;

    public static ReplyRequest of(Integer userId, String comment) {
        return ReplyRequest.builder()
                .userId(userId)
                .comment(comment).build();
    }
}
