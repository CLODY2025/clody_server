package com.clody.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class MemberRequestDTO {

    @Getter
    @NoArgsConstructor
    @Schema(description = "이메일 발송 요청")
    public static class SendEmailVerification {
        @Schema(description = "이메일", example = "user@example.com")
        @Email(message = "올바른 이메일 형식이어야 합니다")
        @NotBlank(message = "이메일은 필수입니다")
        private String email;
    }

    @Getter
    @NoArgsConstructor
    @Schema(description = "이메일 인증 요청")
    public static class VerifyEmail {
        @Schema(description = "이메일", example = "user@example.com")
        @Email(message = "올바른 이메일 형식이어야 합니다")
        @NotBlank(message = "이메일은 필수입니다")
        private String email;

        @Schema(description = "인증번호", example = "123456")
        @NotBlank(message = "인증번호는 필수입니다")
        @Pattern(regexp = "^[0-9]{6}$", message = "인증번호는 6자리 숫자여야 합니다")
        private String verificationCode;
    }

    @Getter
    @NoArgsConstructor
    @Schema(description = "닉네임 중복 확인 요청")
    public static class CheckNickname {
        @Schema(description = "닉네임", example = "nickname123")
        @NotBlank(message = "닉네임은 필수입니다")
        @Size(min = 2, max = 20, message = "닉네임은 2-20자 이내여야 합니다")
        @Pattern(regexp = "^[가-힣a-zA-Z0-9_-]*$", message = "닉네임은 한글, 영문, 숫자, '_', '-'만 사용 가능합니다")
        private String nickname;
    }

    @Getter
    @NoArgsConstructor
    @Schema(description = "회원가입 요청")
    public static class SignUp {
        @Schema(description = "이메일", example = "user@example.com")
        @Email(message = "올바른 이메일 형식이어야 합니다")
        @NotBlank(message = "이메일은 필수입니다")
        private String email;

        @Schema(description = "비밀번호", example = "Password123!")
        @NotBlank(message = "비밀번호는 필수입니다")
        private String password;

        @Schema(description = "닉네임", example = "nickname123")
        @NotBlank(message = "닉네임은 필수입니다")
        @Size(min = 2, max = 20, message = "닉네임은 2-20자 이내여야 합니다")
        @Pattern(regexp = "^[가-힣a-zA-Z0-9_-]*$", message = "닉네임은 한글, 영문, 숫자, '_', '-'만 사용 가능합니다")
        private String nickname;

        @Schema(description = "인증번호", example = "123456")
        @NotBlank(message = "인증번호는 필수입니다")
        @Pattern(regexp = "^[0-9]{6}$", message = "인증번호는 6자리 숫자여야 합니다")
        private String verificationCode;
    }
}