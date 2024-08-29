package com.github.project3.dto.chat;

import lombok.Data;

@Data
public class ChatUsersRequest {
    private Integer id;
    private String username;
    private String password;
}
