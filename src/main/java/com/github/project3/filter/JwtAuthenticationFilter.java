package com.github.project3.filter;

import com.github.project3.entity.user.CustomUserDetails;
import com.github.project3.jwt.JwtTokenProvider;
import com.github.project3.service.exceptions.JwtTokenException;
import com.github.project3.service.exceptions.NotFoundException;
import com.github.project3.service.user.CustomUserDetailsService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService customUserDetailsService;


    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        String method = request.getMethod();
        log.info("Request URI: {}", requestURI);

        // Swagger 관련 경로 필터 통과
        if (requestURI.startsWith("/swagger-ui/")
                || requestURI.startsWith("/v3/")
                || requestURI.startsWith("/swagger-resources/")
                || requestURI.startsWith("/webjars/")
                || "/api/user/login".equals(requestURI)
                || "/api/user/signup".equals(requestURI)
                || "/api/user/logout".equals(requestURI)
                || requestURI.startsWith("/api/camp/campName")
                || "/api/camps".equals(requestURI) && "GET".equals(request.getMethod())
                || requestURI.startsWith("/api/camp/addr")
                || requestURI.startsWith("/api/camp/category")
                || requestURI.matches("/api/camps/\\d+")
                || requestURI.startsWith("/api/admin/notice/all")
                || requestURI.matches("/api/admin/notice/\\d+")
                || "/api/reviews".equals(requestURI) && "GET".equals(request.getMethod())
                || requestURI.matches("/api/reviews/camp/\\d+")
                || requestURI.matches("/api/reviews/\\d+/replies") && "GET".equals(request.getMethod()))
        {
            filterChain.doFilter(request, response);
            return;
        }


        // 이 외의 모든 요청에 대해 JWT 인증 필요
        String jwtToken = jwtTokenProvider.resolveToken(request);
        log.info("Extracted JWT Token: {}", jwtToken);


        if (jwtToken == null) {
            log.warn("Access denied due to missing JWT token");
            sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "JWT token is null");
            return;

        }

        if (!jwtTokenProvider.validateToken(jwtToken)) {
            log.warn("Access denied due to invalid JWT token");
            sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "Invalid Token");
            return;
        }


        String tokenLoginId = jwtTokenProvider.getLoginid(jwtToken);
        log.info("Extracted loginId from Token: {}", tokenLoginId);

        if (tokenLoginId == null || tokenLoginId.isEmpty()) {
            log.warn("loginId is missing in the token");
            sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "로그인ID가 토큰에 없습니다.");
            return;
        }

        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                // 사용자의 세부 정보를 로드
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(tokenLoginId);
                log.info("User Details: {}", userDetails);

                if (userDetails instanceof CustomUserDetails) {
                    CustomUserDetails customUserDetail = (CustomUserDetails) userDetails;
                    log.info("User ID: {}", customUserDetail.getId());
                    log.info("User loginId: {}", customUserDetail.getUsername());

                    // loginId가 토큰에 포함된 ID와 일치하는지 확인
                    if (!customUserDetail.getUsername().equals(tokenLoginId)) {
                        log.warn("Token loginId does not match the user loginId");
                        sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "로그인ID와 토큰ID가 일치하지 않습니다.");
                        return;
                    }
                }

                // 토큰 유효성 검사
                if (jwtTokenProvider.validateToken(jwtToken)) {
                    // 인증 객체 생성
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // 인증 객체를 SecurityContext에 설정
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.info("Authentication successful");
                } else {
                    log.warn("Invalid token");
                    throw new JwtTokenException("INVALID TOKEN");
                }
            } catch (JwtTokenException e) {
                log.error("Token validation failed: ", e);
                sendErrorResponse(response, HttpStatus.UNAUTHORIZED, e.getMessage());
                return;
            }
        }

        // JWT 인증을 통과한 경우 요청을 계속 진행
        filterChain.doFilter(request, response);
    }

    private void sendErrorResponse(HttpServletResponse response, HttpStatus status, String message) throws IOException {
        response.setStatus(status.value());
        response.setContentType("application/json");
        response.getWriter().write("{\"error\":\"" + message + "\"}");
    }

}













