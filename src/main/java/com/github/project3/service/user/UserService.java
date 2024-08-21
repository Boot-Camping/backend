package com.github.project3.service.user;


import com.github.project3.dto.cash.CashRequest;
import com.github.project3.dto.user.request.LoginRequest;
import com.github.project3.dto.user.request.SignupRequest;
import com.github.project3.dto.user.request.TokenRequest;
import com.github.project3.dto.user.response.LoginResponse;
import com.github.project3.dto.user.response.SignupResponse;
import com.github.project3.entity.user.RefreshEntity;
import com.github.project3.entity.user.UserEntity;
import com.github.project3.entity.user.enums.TransactionType;
import com.github.project3.jwt.JwtTokenProvider;
import com.github.project3.repository.user.RefreshRepository;
import com.github.project3.repository.user.UserRepository;
import com.github.project3.service.cash.CashService;
import com.github.project3.service.exceptions.NotFoundException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshRepository refreshRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final CashService cashService;


    public SignupResponse signup(SignupRequest signupRequest) {

        if (userRepository.findByLoginId(signupRequest.getLoginId()).isPresent()) {
            // 중복된 loginId가 있으면 예외 발생
            throw new IllegalArgumentException("중복된 loginId가 있습니다.");
        }

        UserEntity user = userRepository.findByLoginId(signupRequest.getLoginId())
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

    public LoginResponse login(LoginRequest loginRequest, HttpServletResponse response) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginRequest.getLoginId(), loginRequest.getPassword());

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserEntity user = userRepository.findByLoginId(loginRequest.getLoginId())
                .orElseThrow(() -> new UsernameNotFoundException("loginId 해당하는 유저가 없습니다: " + loginRequest.getLoginId()));

        String accessToken = jwtTokenProvider.createToken("access", user.getLoginId(), user.getId(), 3600000L);
        String refreshToken = jwtTokenProvider.createToken("refresh", user.getLoginId(), user.getId(), 86400000L);

        addRefreshEntity(user.getLoginId(),refreshToken, 86400000L );

        response.setHeader("access", accessToken);
        response.addCookie(createCookie("refresh", refreshToken));
        response.setStatus(HttpStatus.OK.value());

        TokenRequest tokenRequest = new TokenRequest(accessToken, refreshToken);

        return new LoginResponse("로그인에 성공했습니다", tokenRequest);
    }

    private Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24*60*60);
        cookie.setHttpOnly(true);

        return cookie;
    }

    private void addRefreshEntity(String loginId, String refresh, Long expiredMs) {
        Date date = new Date(System.currentTimeMillis() + expiredMs);

        UserEntity user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        refreshRepository.save(RefreshEntity.builder()
                .loginId(loginId)
                .refresh(refresh)
                .expiration(date.toString())
                .user(user)
                .build());
    }


    public Integer chargeCash(CashRequest cashRequest, Integer userId) {
        UserEntity user = userRepository.findById(userId).orElseThrow(()-> new NotFoundException("해당 ID의 사용자가 존재하지 않습니다."));

        return cashService.processTransaction(user, cashRequest.getCash(), TransactionType.DEPOSIT);
    }
}
