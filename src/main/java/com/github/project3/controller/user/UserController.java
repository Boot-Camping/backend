package com.github.project3.controller.user;


import com.github.project3.dto.user.request.LoginRequest;
import com.github.project3.dto.user.request.SignupRequest;
import com.github.project3.dto.user.response.LoginResponse;
import com.github.project3.dto.user.response.SignupResponse;
import com.github.project3.service.user.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
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
    public ResponseEntity<SignupResponse> signup(@Valid @RequestBody SignupRequest signupRequest) {
        SignupResponse response = userService.signup(signupRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        LoginResponse loginResponse = userService.login(loginRequest, response);
        return ResponseEntity.ok(loginResponse);
    }
}
