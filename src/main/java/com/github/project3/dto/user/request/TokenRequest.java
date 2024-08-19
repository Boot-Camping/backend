package com.github.project3.dto.user.request;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class TokenRequest {

    private String accessToken;
    private String refreshToken;
}
