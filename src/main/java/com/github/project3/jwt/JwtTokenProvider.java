package com.github.project3.jwt;


import com.github.project3.service.exceptions.JwtTokenException;
import io.jsonwebtoken.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Base64;
import java.util.Date;

@Slf4j
@Component
public class JwtTokenProvider {

    private final String secretKey;
    private final UserDetailsService userDetailsService;

    public JwtTokenProvider(@Value("${JWT_SECRET_KEY}") String secretKey, UserDetailsService userDetailsService) {
        this.secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
        this.userDetailsService = userDetailsService;
    }

    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        return bearerToken;
    }

    public Boolean validateToken(String accessToken) {
        try {
            Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(accessToken).getBody();
            Date now = new Date();
            return !claims.getExpiration().before(now); // 만료시간이 현재 시간 이전인지 여부 확인
        } catch (ExpiredJwtException e) {
            // 토큰 만료 예외 처리
            throw new JwtTokenException("토큰이 만료되었습니다.", e);
        } catch (MalformedJwtException e) {
            // 토큰 형식 오류 예외 처리
            throw new JwtTokenException("잘못된 토큰 형식입니다.", e);
        } catch (SignatureException e) {
            // 토큰 서명 오류 예외 처리
            throw new JwtTokenException("토큰 서명이 유효하지 않습니다.", e);
        } catch (IllegalArgumentException e) {
            // 기타 JWT 오류 처리
            throw new JwtTokenException("토큰이 비어 있거나 잘못되었습니다.", e);
        }
    }


    public Authentication getAuthentication(String accessToken) {
        Claims claims = parseClaims(accessToken);

        UserDetails userDetails = userDetailsService.loadUserByUsername(claims.getSubject());

        return new UsernamePasswordAuthenticationToken(userDetails, accessToken);
    }

    public String createToken(String tokenType, String loginId, Integer id, Long expiredMs) {
        Claims claims = Jwts.claims()
                .setSubject(loginId);
        claims.put("tokenType", tokenType);
        claims.put("userId", id);

        Date now = new Date();

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + expiredMs))
                .signWith(SignatureAlgorithm.HS512, secretKey)
                .compact();
    }

    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(accessToken).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }


    public Integer getUserId(String jwtToken) {
        return parseClaims(jwtToken).get("userId", Integer.class);
    }

    public String getLoginid(String jwtToken) {
        return parseClaims(jwtToken).getSubject();
    }

    public Boolean isNotExpired(String jwtToken) {
        return parseClaims(jwtToken).getExpiration().after(new Date());
    }
}




