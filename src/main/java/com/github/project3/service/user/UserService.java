package com.github.project3.service.user;

import com.github.project3.dto.user.SignupDTO;
import com.github.project3.dto.user.SignupRequest;
import com.github.project3.dto.user.response.SignupResponse;
import com.github.project3.entity.user.UserEntity;
import com.github.project3.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public SignupResponse signup(SignupRequest signupRequest) {

        UserEntity user = userRepository.findByEmail(signupRequest.getEmail())
                .orElseGet(() -> userRepository.save(UserEntity.builder()
                        .email(signupRequest.getEmail())
                        .password(passwordEncoder.encode(signupRequest.getPassword()))
                        .loginId(signupRequest.getLoginId())
                        .name(signupRequest.getName())
                        .tel(signupRequest.getTel())
                        .addr(signupRequest.getAddr())
                        .build()));

        return new SignupResponse("회원가입에 성공했습니다");
    }
}
