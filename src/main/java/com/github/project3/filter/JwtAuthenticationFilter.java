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
                || requestURI.matches("/api/review/camp/\\d+")) {
            filterChain.doFilter(request, response);
            return;
        }


        String jwtToken = jwtTokenProvider.resolveToken(request);
        log.info("Extracted JWT Token: " + jwtToken);

        if (jwtToken != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            String LoginId = jwtTokenProvider.getLoginid(jwtToken);
            log.info("Extracted loginId: " + LoginId);

            try {
                // 사용자 세부 정보 로드
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(LoginId);
                log.info("User Details: " + userDetails);

                if (userDetails instanceof CustomUserDetails) {
                    CustomUserDetails customUserDetail = (CustomUserDetails) userDetails;
                    log.info("User ID: " + customUserDetail.getId());
                    log.info("User loginId: " + customUserDetail.getUsername());
                    log.info("User Password: " + customUserDetail.getPassword());
                }

                // 토큰 유효성 검사
                if (jwtTokenProvider.validateToken(jwtToken)) {
                    // 인증 객체 생성
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // 인증 객체를 SecurityContext에 설정
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.info("인증 성공");
                } else {
                    log.warn("유효하지 않은 토큰입니다.");
                }
            } catch (Exception e) {
                log.error("토큰 검증 실패: ", e);
            }


        }
    }
}


