package com.clody.domain.member.dto;

import com.clody.domain.member.entity.AccountScope;
import com.clody.domain.member.entity.Member;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class MemberResponseDTO {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "이메일 발송 응답")
    public static class SendEmailVerification {
        @Schema(description = "메시지", example = "인증번호가 이메일로 발송되었습니다")
        private String message;

        @Schema(description = "만료 시간", example = "2024-01-01T10:30:00")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime expiresAt;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "이메일 인증 응답")
    public static class VerifyEmail {
        @Schema(description = "메시지", example = "이메일 인증이 완료되었습니다")
        private String message;

        @Schema(description = "인증 상태", example = "true")
        private Boolean verified;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "닉네임 중복 확인 응답")
    public static class CheckNickname {
        @Schema(description = "사용 가능 여부", example = "true")
        private Boolean available;

        @Schema(description = "메시지", example = "사용 가능한 닉네임입니다")
        private String message;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "회원가입 응답")
    public static class SignUp {
        @Schema(description = "회원 ID", example = "1")
        private Long memberId;

        @Schema(description = "이메일", example = "user@example.com")
        private String email;

        @Schema(description = "닉네임", example = "nickname123")
        private String nickname;

        @Schema(description = "액세스 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        private String accessToken;

        @Schema(description = "리프레시 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        private String refreshToken;

        @Schema(description = "토큰 만료 시간", example = "2024-01-01T10:30:00")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime tokenExpiresAt;

        public static SignUp from(Member member, String accessToken, String refreshToken, LocalDateTime tokenExpiresAt) {
            return SignUp.builder()
                    .memberId(member.getId())
                    .email(member.getEmail())
                    .nickname(member.getNickname())
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .tokenExpiresAt(tokenExpiresAt)
                    .build();
        }
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "회원 정보 응답")
    public static class MemberInfo {
        @Schema(description = "회원 ID", example = "1")
        private Long memberId;

        @Schema(description = "이메일", example = "user@example.com")
        private String email;

        @Schema(description = "닉네임", example = "nickname123")
        private String nickname;

        @Schema(description = "프로필 이미지 URL", example = "https://example.com/profile.jpg")
        private String profileImageUrl;

        @Schema(description = "이메일 인증 여부", example = "true")
        private Boolean isEmailVerified;

        @Schema(description = "가입일", example = "2024-01-01T10:30:00")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime createdAt;

        public static MemberInfo from(Member member) {
            return MemberInfo.builder()
                    .memberId(member.getId())
                    .email(member.getEmail())
                    .nickname(member.getNickname())
                    .profileImageUrl(member.getProfileImageUrl())
                    .isEmailVerified(member.getIsEmailVerified())
                    .createdAt(member.getCreatedAt())
                    .build();
        }
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "회원 프로필 응답")
    public static class MemberProfile {
        @Schema(description = "회원 ID", example = "1")
        private Long id;

        @Schema(description = "이메일", example = "user@example.com")
        private String email;

        @Schema(description = "닉네임", example = "nickname123")
        private String nickname;

        @Schema(description = "프로필 이미지 URL", example = "https://example.com/profile.jpg")
        private String profileImageUrl;

        @Schema(description = "계정 공개 범위", example = "PUBLIC")
        private AccountScope accountScope;

        @Schema(description = "가입일", example = "2024-01-01T10:30:00")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime createdAt;

        public static MemberProfile from(Member member) {
            return MemberProfile.builder()
                    .id(member.getId())
                    .email(member.getEmail())
                    .nickname(member.getNickname())
                    .profileImageUrl(member.getProfileImageUrl())
                    .accountScope(member.getAccountScope())
                    .createdAt(member.getCreatedAt())
                    .build();
        }
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "계정 범위 변경 응답")
    public static class UpdateAccountScope {
        @Schema(description = "회원 ID", example = "1")
        private Long memberId;

        @Schema(description = "변경된 계정 공개 범위", example = "PUBLIC")
        private AccountScope accountScope;

        @Schema(description = "변경 완료 메시지", example = "계정이 공개로 설정되었습니다")
        private String message;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "비밀번호 변경용 이메일 인증번호 발송 응답")
    public static class SendPasswordResetVerification {
        @Schema(description = "메시지", example = "비밀번호 변경용 인증번호가 이메일로 발송되었습니다")
        private String message;

        @Schema(description = "만료 시간", example = "2024-01-01T10:30:00")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime expiresAt;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "비밀번호 변경용 이메일 인증번호 검증 응답")
    public static class VerifyPasswordResetCode {
        @Schema(description = "메시지", example = "인증번호 확인이 완료되었습니다")
        private String message;

        @Schema(description = "인증 상태", example = "true")
        private Boolean verified;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "비밀번호 변경 응답")
    public static class ChangePassword {
        @Schema(description = "회원 ID", example = "1")
        private Long memberId;

        @Schema(description = "메시지", example = "비밀번호가 성공적으로 변경되었습니다")
        private String message;

        @Schema(description = "변경 시간", example = "2024-01-01T10:30:00")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime changedAt;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "프로필 이미지 업로드 응답")
    public static class UploadProfileImage {
        @Schema(description = "회원 ID", example = "1")
        private Long memberId;

        @Schema(description = "업로드된 이미지 URL", example = "https://clodybucket.s3.ap-northeast-2.amazonaws.com/profile/uuid/image.jpg")
        private String profileImageUrl;

        @Schema(description = "메시지", example = "프로필 이미지가 성공적으로 업데이트되었습니다")
        private String message;

        @Schema(description = "업로드 시간", example = "2024-01-01T10:30:00")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime uploadedAt;
    }
}