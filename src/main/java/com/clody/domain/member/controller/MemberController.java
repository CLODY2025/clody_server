package com.clody.domain.member.controller;

import com.clody.domain.member.dto.MemberRequestDTO;
import com.clody.domain.member.dto.MemberResponseDTO;
import com.clody.domain.member.service.MemberCommandService;
import com.clody.domain.member.service.MemberQueryService;
import com.clody.global.apiPayload.ApiResponse;
import com.clody.global.email.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
@Tag(name = "Member", description = "회원 관련 API")
public class MemberController {

    private final MemberCommandService memberCommandService;
    private final MemberQueryService memberQueryService;
    private final EmailService emailService;

    @PostMapping("/email/send")
    @Operation(summary = "이메일 인증번호 발송", description = "회원가입을 위한 이메일 인증번호를 발송합니다.")
    public ApiResponse<MemberResponseDTO.SendEmailVerification> sendEmailVerification(
            @Valid @RequestBody MemberRequestDTO.SendEmailVerification request) {
        
        log.info("이메일 인증번호 발송 요청 - email: {}", request.getEmail());
        
        MemberResponseDTO.SendEmailVerification response = memberCommandService.sendEmailVerification(request);
        return ApiResponse.onSuccess(response);
    }

    @PostMapping("/email/verify")
    @Operation(summary = "이메일 인증번호 검증", description = "발송된 이메일 인증번호를 검증합니다.")
    public ApiResponse<MemberResponseDTO.VerifyEmail> verifyEmail(
            @Valid @RequestBody MemberRequestDTO.VerifyEmail request) {
        
        log.info("이메일 인증번호 검증 요청 - email: {}", request.getEmail());
        
        MemberResponseDTO.VerifyEmail response = memberCommandService.verifyEmail(request);
        return ApiResponse.onSuccess(response);
    }

    @GetMapping("/nickname/check")
    @Operation(summary = "닉네임 중복 확인", description = "닉네임 사용 가능 여부를 확인합니다.")
    public ApiResponse<MemberResponseDTO.CheckNickname> checkNickname(
            @RequestParam("nickname") String nickname) {
        
        log.info("닉네임 중복 확인 요청 - nickname: {}", nickname);
        
        // 수동으로 validation 체크 (RequestParam은 @Valid가 적용되지 않음)
        if (nickname == null || nickname.trim().isEmpty()) {
            throw new IllegalArgumentException("닉네임은 필수입니다");
        }
        if (nickname.length() < 2 || nickname.length() > 20) {
            throw new IllegalArgumentException("닉네임은 2-20자 이내여야 합니다");
        }
        if (!nickname.matches("^[가-힣a-zA-Z0-9_-]*$")) {
            throw new IllegalArgumentException("닉네임은 한글, 영문, 숫자, '_', '-'만 사용 가능합니다");
        }
        
        MemberResponseDTO.CheckNickname response = memberQueryService.checkNickname(nickname);
        return ApiResponse.onSuccess(response);
    }

    @PostMapping("/signup")
    @Operation(summary = "회원가입", description = "이메일 인증을 완료한 후 회원가입을 진행합니다.")
    public ApiResponse<MemberResponseDTO.SignUp> signUp(
            @Valid @RequestBody MemberRequestDTO.SignUp request) {
        
        log.info("회원가입 요청 - email: {}, nickname: {}", request.getEmail(), request.getNickname());
        
        MemberResponseDTO.SignUp response = memberCommandService.signUp(request);
        return ApiResponse.onSuccess(response);
    }

    @PostMapping("/email/test")
    @Operation(summary = "이메일 발송 테스트", description = "SMTP 연결을 테스트하기 위한 테스트 이메일을 발송합니다.")
    public ApiResponse<String> sendTestEmail(@RequestParam("email") String email) {
        
        log.info("테스트 이메일 발송 요청 - email: {}", email);
        
        try {
            emailService.sendTestEmail(email);
            return ApiResponse.onSuccess("테스트 이메일이 성공적으로 발송되었습니다.");
        } catch (Exception e) {
            log.error("테스트 이메일 발송 실패: {}", e.getMessage(), e);
            return ApiResponse.onSuccess("테스트 이메일 발송에 실패했습니다: " + e.getMessage());
        }
    }
}