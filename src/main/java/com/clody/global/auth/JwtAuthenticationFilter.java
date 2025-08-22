package com.clody.global.auth;

import com.clody.global.jwt.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) 
            throws ServletException, IOException {
        
        log.debug("JWT Filter 실행 - URI: {}", request.getRequestURI());
        
        String token = extractTokenFromHeader(request);
        log.debug("추출된 토큰: {}", token != null ? "토큰 존재" : "토큰 없음");
        
        if (token != null) {
            boolean isValid = jwtUtil.validateToken(token);
            log.debug("토큰 검증 결과: {}", isValid);
            
            if (isValid) {
                Long memberId = jwtUtil.getMemberIdFromToken(token);
                log.debug("토큰에서 추출된 memberId: {}", memberId);
                
                Authentication authentication = new UsernamePasswordAuthenticationToken(
                    memberId, null, Collections.emptyList()
                );
                
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.debug("SecurityContext에 인증 정보 설정 완료");
            }
        }
        
        filterChain.doFilter(request, response);
    }

    private String extractTokenFromHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        log.debug("Authorization 헤더: {}", bearerToken);
        
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            String token = bearerToken.substring(7);
            log.debug("Bearer 제거 후 토큰: {}", token.substring(0, Math.min(token.length(), 20)) + "...");
            return token;
        }
        
        return null;
    }
}