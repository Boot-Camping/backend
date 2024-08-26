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
        log.info("Request URI: {}", requestURI);

        // Swagger 관련 경로 필터 통과
        if (requestURI.startsWith("/swagger-ui/")
                || requestURI.startsWith("/v3/")
                || requestURI.startsWith("/swagger-resources/")
                || requestURI.startsWith("/webjars/")
                || "/api/user/login".equals(requestURI)
                || "/api/user/signup".equals(requestURI)
                || "/api/user/logout".equals(requestURI)
                || "/api/camp".equals(requestURI)
                || requestURI.startsWith("/api/camp/campName")
                || requestURI.startsWith("/api/camp/addr")
                || requestURI.startsWith("/api/camp/category")
                || requestURI.matches("/api/camp/\\d+")
                || requestURI.startsWith("/api/admin/notice/all")
                || requestURI.matches("/api/admin/notice/\\d+")
                || "/api/review/all".equals(requestURI)
                || requestURI.matches("/api/review/camp/\\d+")
                || requestURI.matches("/api/review/user/\\d+")
                || requestURI.matches("/api/reply/review/\\d+")) {
            filterChain.doFilter(request, response);
            return;
        }


        String jwtToken = jwtTokenProvider.resolveToken(request);
        log.info("Extracted JWT Token: " + jwtToken);


        if (jwtToken == null) {
            log.warn("JWT Token is missing");
            throw new JwtTokenException("TOKEN IS NULL");
        }

        String tokenLoginId = jwtTokenProvider.getLoginid(jwtToken);
        log.info("Extracted loginId from Token: {}", tokenLoginId);

        if (tokenLoginId == null || tokenLoginId.isEmpty()) {
            log.warn("loginId is missing in the token");
            throw new JwtTokenException("로그인ID가 토큰에 없습니다.");
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
                        throw new JwtTokenException("로그인ID와 토큰ID가 일치하지 않습니다.");
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
                throw new JwtTokenException("Token validation falied");

            }
        }

        filterChain.doFilter(request, response);
    }





    }





