package com.github.project3.filter;

import com.github.project3.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter {

    private final JwtTokenProvider jwtTokenProvider;
}
