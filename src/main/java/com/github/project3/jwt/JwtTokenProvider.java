package com.github.project3.jwt;


import com.github.project3.entity.user.UserEntity;
import com.github.project3.entity.user.enums.Status;
import com.github.project3.repository.user.UserRepository;
import com.github.project3.service.exceptions.NotAcceptException;
import com.github.project3.service.exceptions.NotFoundException;
import io.jsonwebtoken.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.Date;

@Slf4j
@Component
public class JwtTokenProvider {

    private final String secretKey;
    private final UserDetailsService userDetailsService;
    private final UserRepository userRepository;

    public JwtTokenProvider(@Value("${JWT_SECRET_KEY}") String secretKey, UserDetailsService userDetailsService, UserRepository userRepository) {
        this.secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
        this.userDetailsService = userDetailsService;
        this.userRepository = userRepository;
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
        } catch (Exception e) {
            return false;
        }

    }

    public Authentication getAuthentication(String accessToken) {
        Claims claims = parseClaims(accessToken);

        UserDetails userDetails = userDetailsService.loadUserByUsername(claims.getSubject());

        Integer userId = getUserId(accessToken);
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("존재하지 않는 유저 입니다."));
        if (user.getStatus() == Status.BLACKLIST){
            throw new NotAcceptException("블랙리스트 유저는 로그인이 불가능합니다.");
        }

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

    public Boolean isNotExpired(String jwtToken) {
        return parseClaims(jwtToken).getExpiration().after(new Date());
    }
}




