package com.github.project3.controller.user;

import com.github.project3.dto.user.SignupDTO;
import com.github.project3.dto.user.response.SignupResponse;
import com.github.project3.service.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<SignupResponse> signup(@Valid @RequestBody SignupDTO signupDTO) {
        SignupResponse response = userService.signup(signupDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
