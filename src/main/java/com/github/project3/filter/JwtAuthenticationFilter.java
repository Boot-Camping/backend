package com.github.project3.filter;

import com.github.project3.jwt.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;


    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        log.info("Request URI: " + requestURI);

        // Swagger 관련 경로 필터 통과
        if (requestURI.startsWith("/swagger-ui/")
                || requestURI.startsWith("/v3/")
                || requestURI.startsWith("/swagger-resources/")
                || requestURI.startsWith("/webjars/")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 회원가입, 로그인, 물품 조회 요청 필터 통과
        if ("/api/user/login".equals(requestURI)
                || "/api/user/signup".equals(requestURI)
                || "/api/camp".equals(requestURI)
                ) {
            filterChain.doFilter(request, response);
            return;
        }

        // JWT 토큰 검증
        String jwtToken = jwtTokenProvider.resolveToken(request);
        log.info("jwtToken = " + jwtToken);

        if (jwtToken != null && jwtTokenProvider.validateToken(jwtToken)) {
            Authentication authentication = jwtTokenProvider.getAuthentication(jwtToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // JWT 만료 체크
        if (jwtToken != null && !jwtTokenProvider.isNotExpired(jwtToken)) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return;
        }

        // 필터 체인 계속 진행
        filterChain.doFilter(request, response);
    }
}
