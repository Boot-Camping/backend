package com.github.project3.dto.reply;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateReplyRequest {
    private Integer userId;
    private String comment;

    public static UpdateReplyRequest of(Integer userId, String comment) {
        return UpdateReplyRequest.builder()
                .userId(userId)
                .comment(comment)
                .build();
    }
}
