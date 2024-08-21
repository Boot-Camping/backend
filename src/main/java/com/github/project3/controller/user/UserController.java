package com.github.project3.controller.user;


import com.github.project3.dto.cash.CashRequest;
import com.github.project3.dto.user.request.LoginRequest;
import com.github.project3.dto.user.request.SignupRequest;
import com.github.project3.dto.user.response.LoginResponse;
import com.github.project3.dto.user.response.SignupResponse;
import com.github.project3.service.cash.CashService;
import com.github.project3.service.user.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final CashService cashService;

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

    @PutMapping("/chargeCash/{userId}")
    public ResponseEntity<String> cashCharge(@RequestBody CashRequest cashRequest,
                                                   @PathVariable Integer userId){
        Integer cash = userService.chargeCash(cashRequest, userId);
        return ResponseEntity.ok(cash + " 원이 충전되었습니다.");
    }

    @PostMapping(value = "/logout")
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = null;

        // 쿠키에서 refreshToken 값을 찾기
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refresh".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                    break;  // 필요한 쿠키를 찾으면 루프 종료
                }
            }
        }

        // 로그아웃 서비스 호출 및 쿠키 처리
        Cookie logoutCookie = userService.logout(refreshToken);

        // 클라이언트에 로그아웃 쿠키 추가
        response.addCookie(logoutCookie);

        // 로그아웃 완료 메시지 응답 반환
        return ResponseEntity.status(HttpStatus.OK).body("로그아웃이 완료되었습니다.");
    }
}
