package com.github.project3.service.user;

import com.github.project3.dto.user.SignupDTO;
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

    public SignupResponse signup(SignupDTO signupDTO) {

        UserEntity user = userRepository.findByEmail(signupDTO.getEmail())
                .orElseGet(() -> userRepository.save(UserEntity.builder()
                        .email(signupDTO.getEmail())
                        .password(passwordEncoder.encode(signupDTO.getPassword()))
                        .loginId(signupDTO.getLoginId())
                        .name(signupDTO.getName())
                        .tel(signupDTO.getTel())
                        .addr(signupDTO.getAddr())
                        .build()));

        return new SignupResponse("회원가입에 성공했습니다");
    }
}
