package com.clody.domain.auth.controller;

import com.clody.domain.auth.dto.AuthRequestDTO;
import com.clody.domain.auth.dto.AuthResponseDTO;
import com.clody.domain.auth.service.AuthService;
import com.clody.global.apiPayload.ApiResponse;
import com.clody.global.jwt.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "인증 관련 API")
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인하고 JWT 토큰을 발급받습니다.")
    public ApiResponse<AuthResponseDTO.Login> login(
            @Valid @RequestBody AuthRequestDTO.Login request) {
        
        log.info("로그인 요청 - email: {}", request.getEmail());
        
        AuthResponseDTO.Login response = authService.login(request);
        return ApiResponse.onSuccess(response);
    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃", description = "로그아웃하고 저장된 RefreshToken을 삭제합니다.")
    public ApiResponse<AuthResponseDTO.Logout> logout(
            @Parameter(description = "Bearer {token}", required = true, example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        
        try {
            // Authorization 헤더에서 토큰 추출
            String token = extractTokenFromHeader(authorizationHeader);
            
            // 토큰 검증
            if (!jwtUtil.validateToken(token)) {
                log.warn("유효하지 않은 토큰으로 로그아웃 시도");
                return ApiResponse.onSuccess(AuthResponseDTO.Logout.of("이미 로그아웃된 상태입니다"));
            }
            
            Long memberId = jwtUtil.getMemberIdFromToken(token);
            log.info("로그아웃 요청 - memberId: {}", memberId);
            
            AuthResponseDTO.Logout response = authService.logout(memberId);
            return ApiResponse.onSuccess(response);
            
        } catch (Exception e) {
            log.warn("로그아웃 처리 중 오류: {}", e.getMessage());
            return ApiResponse.onSuccess(AuthResponseDTO.Logout.of("로그아웃 처리되었습니다"));
        }
    }

    @PostMapping("/refresh")
    @Operation(summary = "토큰 재발급", description = "RefreshToken을 사용하여 새로운 AccessToken과 RefreshToken을 발급받습니다.")
    public ApiResponse<AuthResponseDTO.RefreshToken> refreshToken(
            @Valid @RequestBody AuthRequestDTO.RefreshToken request) {
        
        log.info("토큰 재발급 요청");
        
        AuthResponseDTO.RefreshToken response = authService.refreshToken(request);
        return ApiResponse.onSuccess(response);
    }

    private String extractTokenFromHeader(String authorizationHeader) {
        if (authorizationHeader == null || authorizationHeader.trim().isEmpty()) {
            throw new IllegalArgumentException("Authorization 헤더가 없습니다");
        }
        if (!authorizationHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Authorization 헤더 형식이 올바르지 않습니다");
        }
        return authorizationHeader.substring(7);
    }
}