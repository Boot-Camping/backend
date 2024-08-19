package com.github.project3.dto.user.response;


import com.github.project3.dto.user.request.TokenRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponse {
    private String message;
    private TokenRequest tokenRequest;
}
