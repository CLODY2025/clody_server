package com.clody.domain.auth.dto;

import com.clody.domain.member.entity.Member;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class AuthResponseDTO {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "로그인 응답")
    public static class Login {
        @Schema(description = "액세스 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        private String accessToken;

        @Schema(description = "리프레시 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        private String refreshToken;

        @Schema(description = "토큰 만료 시간", example = "2024-01-01T10:30:00")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime tokenExpiresAt;

        @Schema(description = "회원 정보")
        private MemberInfo memberInfo;

        @Getter
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        @Schema(description = "회원 정보")
        public static class MemberInfo {
            @Schema(description = "회원 ID", example = "1")
            private Long memberId;

            @Schema(description = "이메일", example = "user@example.com")
            private String email;

            @Schema(description = "닉네임", example = "nickname123")
            private String nickname;

            @Schema(description = "프로필 이미지 URL", example = "https://example.com/profile.jpg")
            private String profileImageUrl;

            public static MemberInfo from(Member member) {
                return MemberInfo.builder()
                        .memberId(member.getId())
                        .email(member.getEmail())
                        .nickname(member.getNickname())
                        .profileImageUrl(member.getProfileImageUrl())
                        .build();
            }
        }

        public static Login of(String accessToken, String refreshToken, LocalDateTime tokenExpiresAt, Member member) {
            return Login.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .tokenExpiresAt(tokenExpiresAt)
                    .memberInfo(MemberInfo.from(member))
                    .build();
        }
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "로그아웃 응답")
    public static class Logout {
        @Schema(description = "메시지", example = "로그아웃되었습니다")
        private String message;

        @Schema(description = "로그아웃 시간", example = "2024-01-01T10:30:00")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime logoutAt;

        public static Logout of(String message) {
            return Logout.builder()
                    .message(message)
                    .logoutAt(LocalDateTime.now())
                    .build();
        }
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "토큰 재발급 응답")
    public static class RefreshToken {
        @Schema(description = "새로운 액세스 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        private String accessToken;

        @Schema(description = "새로운 리프레시 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        private String refreshToken;

        @Schema(description = "토큰 만료 시간", example = "2024-01-01T10:30:00")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime tokenExpiresAt;

        public static RefreshToken of(String accessToken, String refreshToken, LocalDateTime tokenExpiresAt) {
            return RefreshToken.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .tokenExpiresAt(tokenExpiresAt)
                    .build();
        }
    }
}