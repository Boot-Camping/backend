package com.github.project3.service.user;


import com.github.project3.dto.cash.CashRequest;
import com.github.project3.dto.user.request.LoginRequest;
import com.github.project3.dto.user.request.SignupRequest;
import com.github.project3.dto.user.response.LoginResponse;
import com.github.project3.dto.user.response.SignupResponse;
import com.github.project3.entity.user.CustomUserDetails;
import com.github.project3.entity.user.RefreshEntity;
import com.github.project3.entity.user.UserEntity;
import com.github.project3.entity.user.enums.Status;
import com.github.project3.entity.user.enums.TransactionType;
import com.github.project3.jwt.JwtTokenProvider;
import com.github.project3.repository.user.RefreshRepository;
import com.github.project3.repository.user.UserRepository;
import com.github.project3.service.admin.AuthService;
import com.github.project3.service.cash.CashService;
import com.github.project3.service.exceptions.JwtTokenException;
import com.github.project3.service.exceptions.NotAcceptException;
import com.github.project3.service.exceptions.NotFoundException;
import com.github.project3.service.exceptions.InvalidValueException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshRepository refreshRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final CashService cashService;
    private final AuthService authService;


    public SignupResponse signup(SignupRequest signupRequest) {



        // loginId 중복 확인
        if (userRepository.existsByLoginId(signupRequest.getLoginId())) {
            throw new InvalidValueException("이미 존재하는 loginID입니다.");
        }

        if(!isValidEmail(signupRequest.getEmail())){

            throw new InvalidValueException("이메일형식이 잘못되었습니다");
        }

        if (!isValidPassword(signupRequest.getPassword())) {
            throw new InvalidValueException("비밀번호는 영문자, 숫자, 특수문자의 조합으로 8자 이상 20자 이하로 설정해주세요.");
        }

        if(!isValidLoginId(signupRequest.getLoginId())) {
            throw new InvalidValueException("ID는 특수문자를 제외한 2~10자리여야 합니다");
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

    private boolean isValidPassword(String password) {
        // 비밀번호 패턴 검증 (정규식을 직접 사용할 수도 있음)
        return password != null && password.matches("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-={}|\\[\\]:'\";,.<>?/])[A-Za-z\\d!@#$%^&*()_+\\-={}|\\[\\]:'\";,.<>?/]{8,20}$");
    }

    private boolean isValidLoginId(String LoginId) {

        return LoginId != null && LoginId.matches("^[a-zA-Z0-9_-]{3,15}$");
    }

    private boolean isValidEmail(String email) {
        if (email == null) {
            return false;
        }
        email = email.trim(); // 앞뒤 공백 제거
        return email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$");
    }

    public LoginResponse login(LoginRequest loginRequest, HttpServletResponse response) {
        // UserEntity를 loginId로 조회
        UserEntity foundedUser = userRepository.findByLoginId(loginRequest.getLoginId())
                .orElseThrow(() -> new NotFoundException("loginId를 찾을 수 없습니다."));

        // 비밀번호 비교 (PasswordEncoder 사용)
        if (!passwordEncoder.matches(loginRequest.getPassword(), foundedUser.getPassword())) {
            throw new InvalidValueException("비밀번호가 일치하지 않습니다.");
        }

        if (foundedUser.getStatus() == Status.DELETE) {
            throw new InvalidValueException("삭제된 회원으로 로그인 할 수 없습니다.");
        }

        // 인증 토큰 생성 및 인증 설정
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(foundedUser.getLoginId(), loginRequest.getPassword());

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        authService.verifyNotBlacklisted(foundedUser);

        // JWT 토큰 생성
        String accessToken = jwtTokenProvider.createToken("access", foundedUser.getLoginId(), foundedUser.getId(), 3600000L);
        String refreshToken = jwtTokenProvider.createToken("refresh", foundedUser.getLoginId(), foundedUser.getId(), 86400000L);
        String bearerToken = "Bearer " + accessToken;
        // RefreshToken 저장
        addRefreshEntity(foundedUser.getLoginId(), refreshToken, 86400000L);

        // HTTP 응답에 토큰 설정
        response.setHeader(HttpHeaders.AUTHORIZATION, bearerToken);

        Cookie refreshCookie = createCookie("refresh", refreshToken);
        addCookieWithSameSite(response, refreshCookie, "None"); // SameSite=None
        response.setStatus(HttpStatus.OK.value());



        return new LoginResponse("로그인에 성공했습니다");
    }

    private Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24*60*60);
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // HTTPS에서만 전송 (로컬 개발 시 false)
        cookie.setPath("/");
        cookie.setDomain("boot-camping.netlify.app"); // 도메인 명시 (로컬 개발 시 'localhost' 사용)

        return cookie;
    }

    // SameSite 설정
    private void addCookieWithSameSite(HttpServletResponse response, Cookie cookie, String sameSite) {
        String cookieHeader = String.format("%s=%s; Max-Age=%d; Domain=%s; Path=%s; HttpOnly; SameSite=%s",
                cookie.getName(), cookie.getValue(), cookie.getMaxAge(), cookie.getDomain(), cookie.getPath(), sameSite);

        if ("None".equalsIgnoreCase(sameSite)) {
            cookieHeader += "; Secure"; // HTTPS 환경에서만 쿠키가 전송되도록 설정
        }

        response.addHeader("Set-Cookie", cookieHeader);
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

    public Cookie logout(String refreshToken) {
        if (refreshToken == null) {
            throw new JwtTokenException("리프레시 토큰이 null값입니다");
        }

        Boolean isExist = refreshRepository.existsByRefresh(refreshToken);
        if (!isExist) {
            throw new JwtTokenException("리프레시토큰을 찾을 수 없습니다.");
        }

        refreshRepository.deleteByRefresh(refreshToken);

        Cookie cookie = new Cookie("refresh", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");

        return cookie;
    }

    public void deleteUser(LoginRequest loginRequest) {
        UserEntity foundedUser = userRepository.findByLoginId(loginRequest.getLoginId())
                .orElseThrow(() -> new NotFoundException("loginId를 찾을 수 없습니다."));

        // 비밀번호 비교 (PasswordEncoder 사용)
        if (!passwordEncoder.matches(loginRequest.getPassword(), foundedUser.getPassword())) {
            throw new InvalidValueException("비밀번호가 일치하지 않습니다.");
        }

        if (foundedUser.getStatus() == Status.DELETE) {
            throw new NotAcceptException("이미 삭제 된 회원입니다.");
        }
        foundedUser.setStatus(Status.DELETE);
        userRepository.save(foundedUser);
    }

    // 인증이 완료되어 SecurityContextHolder 저장된 user 의 id로 검색
    public UserEntity findAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof UserDetails) {
            Integer userId = ((CustomUserDetails) authentication.getPrincipal()).getId();
            return userRepository.findById(userId)
                    .orElseThrow(() -> new NotFoundException("해당하는 ID의 유저는 존재하지 않습니다."));
        }
        throw new NotFoundException("인증된 유저를 찾을 수 없습니다.");
    }


    }

