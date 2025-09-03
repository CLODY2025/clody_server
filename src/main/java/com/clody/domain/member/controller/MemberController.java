package com.clody.domain.member.controller;

import com.clody.domain.member.dto.MemberRequestDTO;
import com.clody.domain.member.dto.MemberResponseDTO;
import com.clody.domain.member.entity.Member;
import com.clody.domain.member.service.MemberCommandService;
import com.clody.domain.member.service.MemberQueryService;
import com.clody.global.apiPayload.ApiResponse;
import com.clody.global.auth.CurrentUser;
import com.clody.global.email.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;

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

    @GetMapping("/profile")
    @Operation(
        summary = "로그인한 회원 프로필 조회", 
        description = "JWT 토큰을 통해 현재 로그인한 회원의 프로필 정보를 조회합니다.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    public ApiResponse<MemberResponseDTO.MemberProfile> getCurrentMemberProfile(@CurrentUser Member member) {
        
        log.info("로그인한 회원 프로필 조회 요청 - memberId: {}", member.getId());
        
        MemberResponseDTO.MemberProfile response = memberQueryService.getCurrentMemberProfile(member.getId());
        return ApiResponse.onSuccess(response);
    }

    @PatchMapping("/account-scope")
    @Operation(
        summary = "계정 범위 설정", 
        description = "로그인한 회원의 계정 범위(PUBLIC/FOLLOWERS_ONLY)를 변경합니다.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    public ApiResponse<MemberResponseDTO.UpdateAccountScope> updateAccountScope(
            @CurrentUser Member member,
            @Valid @RequestBody MemberRequestDTO.UpdateAccountScope request) {
        
        log.info("계정 범위 변경 요청 - memberId: {}, accountScope: {}", member.getId(), request.getAccountScope());
        
        MemberResponseDTO.UpdateAccountScope response = memberCommandService.updateAccountScope(member, request);
        return ApiResponse.onSuccess(response);
    }

    @PostMapping("/password/send-verification")
    @Operation(
        summary = "비밀번호 변경용 이메일 인증번호 발송", 
        description = "비밀번호 변경을 위한 이메일 인증번호를 발송합니다."
    )
    public ApiResponse<MemberResponseDTO.SendPasswordResetVerification> sendPasswordResetVerification(
            @Valid @RequestBody MemberRequestDTO.SendPasswordResetVerification request) {
        
        log.info("비밀번호 변경용 이메일 인증번호 발송 요청 - email: {}", request.getEmail());
        
        MemberResponseDTO.SendPasswordResetVerification response = memberCommandService.sendPasswordResetVerification(request);
        return ApiResponse.onSuccess(response);
    }

    @PostMapping("/password/verify")
    @Operation(
        summary = "비밀번호 변경용 이메일 인증번호 검증", 
        description = "발송된 비밀번호 변경용 인증번호를 검증합니다."
    )
    public ApiResponse<MemberResponseDTO.VerifyPasswordResetCode> verifyPasswordResetCode(
            @Valid @RequestBody MemberRequestDTO.VerifyPasswordResetCode request) {
        
        log.info("비밀번호 변경용 인증번호 검증 요청 - email: {}", request.getEmail());
        
        MemberResponseDTO.VerifyPasswordResetCode response = memberCommandService.verifyPasswordResetCode(request);
        return ApiResponse.onSuccess(response);
    }

    @PatchMapping("/password")
    @Operation(
        summary = "비밀번호 변경", 
        description = "이메일 인증을 완료한 후 새로운 비밀번호로 변경합니다.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    public ApiResponse<MemberResponseDTO.ChangePassword> changePassword(
            @CurrentUser Member member,
            @Valid @RequestBody MemberRequestDTO.ChangePassword request) {
        
        log.info("비밀번호 변경 요청 - memberId: {}", member.getId());
        
        MemberResponseDTO.ChangePassword response = memberCommandService.changePassword(member, request);
        return ApiResponse.onSuccess(response);
    }

    @PostMapping(value = "/profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
        summary = "프로필 이미지 업로드",
        description = "회원의 프로필 이미지를 업로드/변경합니다. 기존 이미지가 있으면 삭제 후 새 이미지로 교체됩니다. " +
                     "지원 형식: JPG, JPEG, PNG, GIF, WEBP (최대 5MB)",
        security = @SecurityRequirement(name = "Bearer Authentication"),
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(
                mediaType = MediaType.MULTIPART_FORM_DATA_VALUE
            )
        )
    )
    public ApiResponse<MemberResponseDTO.UploadProfileImage> uploadProfileImage(
            @CurrentUser Member member,
            @Parameter(description = "업로드할 이미지 파일 (JPG, JPEG, PNG, GIF, WEBP, 최대 5MB)", 
                      required = true,
                      content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
            @RequestParam("image") MultipartFile imageFile) {
        
        log.info("프로필 이미지 업로드 요청 - memberId: {}, 파일명: {}", 
                member.getId(), imageFile.getOriginalFilename());
        
        MemberResponseDTO.UploadProfileImage response = memberCommandService.uploadProfileImage(member, imageFile);
        return ApiResponse.onSuccess(response);
    }

    @PatchMapping("/nickname")
    @Operation(
        summary = "닉네임 변경", 
        description = "로그인한 회원의 닉네임을 변경합니다. 닉네임 중복 검사를 포함합니다.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    public ApiResponse<MemberResponseDTO.UpdateNickname> updateNickname(
            @CurrentUser Member member,
            @Valid @RequestBody MemberRequestDTO.UpdateNickname request) {
        
        log.info("닉네임 변경 요청 - memberId: {}, 새 닉네임: {}", member.getId(), request.getNickname());
        
        MemberResponseDTO.UpdateNickname response = memberCommandService.updateNickname(member, request);
        return ApiResponse.onSuccess(response);
    }
}