package com.github.project3.filter;

import com.github.project3.jwt.JwtTokenProvider;
import com.github.project3.service.exceptions.JwtTokenException;
import com.github.project3.service.exceptions.NotFoundException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
                || requestURI.startsWith("/webjars/")
                || "/api/user/login".equals(requestURI)
                || "/api/user/signup".equals(requestURI)
                || "/api/user/logout".equals(requestURI)
                || "/api/camp".equals(requestURI)
                || requestURI.startsWith("/api/camp/category")
                || requestURI.matches("/api/camp/\\d+")
                || requestURI.startsWith("/api/userprofile/notice/all")
                || "/api/review/all".equals(requestURI)
                || requestURI.matches("/api/review/camp/\\d+"))
        {
            filterChain.doFilter(request, response);
            return;
        }



        String jwtToken = jwtTokenProvider.resolveToken(request);
        log.info("jwtToken = " + jwtToken);

        if (jwtToken == null) {
            // JWT가 없는 경우 401 Unauthorized 반환
            sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "TOKEN IS NULL");
            return;
        }

        try {
            if (jwtTokenProvider.validateToken(jwtToken)) {
                Authentication authentication = jwtTokenProvider.getAuthentication(jwtToken);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                // 토큰이 유효하지 않은 경우
                sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다.");
                return;
            }
        } catch (JwtTokenException e) {
            // JWT 예외 발생 시
            sendErrorResponse(response, HttpStatus.UNAUTHORIZED, e.getMessage());
            return;
        }

        // JWT 만료 체크
        if (!jwtTokenProvider.isNotExpired(jwtToken)) {
            sendErrorResponse(response, HttpStatus.BAD_REQUEST, "토큰이 만료되었습니다.");
            return;
        }

        // 필터 체인 계속 진행
        filterChain.doFilter(request, response);
    }

    private void sendErrorResponse(HttpServletResponse response, HttpStatus status, String message) throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"message\":\"" + message + "\"}");
    }
}

