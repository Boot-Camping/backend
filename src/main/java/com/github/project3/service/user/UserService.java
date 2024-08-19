package com.github.project3.service.user;


import com.github.project3.dto.cash.CashRequest;
import com.github.project3.dto.cash.CashResponse;
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
import com.github.project3.service.exceptions.InvalidValueException;
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
import java.util.Optional;

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

        Optional<UserEntity> foundeduser = userRepository.findByLoginId(signupRequest.getLoginId());

        if (foundeduser.isPresent()) {
            throw new InvalidValueException("이미 존재하는 loginID입니다.");
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
        // UserEntity를 loginId로 조회
        UserEntity foundedUser = userRepository.findByLoginId(loginRequest.getLoginId())
                .orElseThrow(() -> new NotFoundException("loginId를 찾을 수 없습니다."));

        // 비밀번호 비교 (PasswordEncoder 사용)
        if (!passwordEncoder.matches(loginRequest.getPassword(), foundedUser.getPassword())) {
            throw new InvalidValueException("잘못된 비밀번호입니다.");
        }

        // 인증 토큰 생성 및 인증 설정
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(foundedUser.getLoginId(), loginRequest.getPassword());

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // JWT 토큰 생성
        String accessToken = jwtTokenProvider.createToken("access", foundedUser.getLoginId(), foundedUser.getId(), 3600000L);
        String refreshToken = jwtTokenProvider.createToken("refresh", foundedUser.getLoginId(), foundedUser.getId(), 86400000L);

        // RefreshToken 저장
        addRefreshEntity(foundedUser.getLoginId(), refreshToken, 86400000L);

        // HTTP 응답에 토큰 설정
        response.setHeader("access", accessToken);
        response.addCookie(createCookie("refresh", refreshToken));
        response.setStatus(HttpStatus.OK.value());

        // TokenRequest 생성
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
