package com.clody.domain.auth.controller;

import com.clody.domain.auth.dto.AuthRequestDTO;
import com.clody.domain.auth.dto.AuthResponseDTO;
import com.clody.domain.auth.service.AuthService;
import com.clody.domain.member.entity.Member;
import com.clody.global.apiPayload.ApiResponse;
import com.clody.global.auth.CurrentUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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

    @PostMapping("/login")
    @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인하고 JWT 토큰을 발급받습니다.")
    public ApiResponse<AuthResponseDTO.Login> login(
            @Valid @RequestBody AuthRequestDTO.Login request) {
        
        log.info("로그인 요청 - email: {}", request.getEmail());
        
        AuthResponseDTO.Login response = authService.login(request);
        return ApiResponse.onSuccess(response);
    }

    @PostMapping("/logout")
    @Operation(
        summary = "로그아웃", 
        description = "로그아웃하고 저장된 RefreshToken을 삭제합니다.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    public ApiResponse<AuthResponseDTO.Logout> logout(@CurrentUser Member member) {
        
        log.info("로그아웃 요청 - memberId: {}", member.getId());
        
        AuthResponseDTO.Logout response = authService.logout(member.getId());
        return ApiResponse.onSuccess(response);
    }

    @PostMapping("/refresh")
    @Operation(summary = "토큰 재발급", description = "RefreshToken을 사용하여 새로운 AccessToken과 RefreshToken을 발급받습니다.")
    public ApiResponse<AuthResponseDTO.RefreshToken> refreshToken(
            @Valid @RequestBody AuthRequestDTO.RefreshToken request) {
        
        log.info("토큰 재발급 요청");
        
        AuthResponseDTO.RefreshToken response = authService.refreshToken(request);
        return ApiResponse.onSuccess(response);
    }
}