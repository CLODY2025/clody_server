package com.clody.domain.auth.exception;

import com.clody.global.apiPayload.code.BaseErrorCode;
import com.clody.global.apiPayload.code.ErrorReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AuthErrorCode implements BaseErrorCode {

    // 로그인 관련 에러
    LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "AUTH4001", "로그인에 실패했습니다."),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "AUTH4002", "이메일 또는 비밀번호가 올바르지 않습니다."),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "AUTH4003", "존재하지 않는 사용자입니다."),
    EMAIL_NOT_VERIFIED(HttpStatus.BAD_REQUEST, "AUTH4004", "이메일 인증이 완료되지 않은 사용자입니다."),
    
    // JWT 토큰 관련 에러
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "JWT4001", "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "JWT4002", "만료된 토큰입니다."),
    UNSUPPORTED_TOKEN(HttpStatus.UNAUTHORIZED, "JWT4003", "지원하지 않는 토큰입니다."),
    CLAIMS_EMPTY(HttpStatus.UNAUTHORIZED, "JWT4004", "토큰 클레임이 비어있습니다."),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "JWT4005", "리프레시 토큰을 찾을 수 없습니다."),
    REFRESH_TOKEN_MISMATCH(HttpStatus.UNAUTHORIZED, "JWT4006", "리프레시 토큰이 일치하지 않습니다."),
    
    // 인증 헤더 관련 에러
    MISSING_AUTHORIZATION_HEADER(HttpStatus.UNAUTHORIZED, "AUTH4005", "Authorization 헤더가 없습니다."),
    INVALID_AUTHORIZATION_HEADER(HttpStatus.UNAUTHORIZED, "AUTH4006", "Authorization 헤더 형식이 올바르지 않습니다."),
    
    // 로그아웃 관련 에러
    LOGOUT_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "AUTH5001", "로그아웃 처리에 실패했습니다."),
    TOKEN_REFRESH_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "AUTH5002", "토큰 재발급에 실패했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    @Override
    public ErrorReasonDTO getReasonHttpStatus() {
        return ErrorReasonDTO.builder()
                .httpStatus(this.status)
                .isSuccess(false)
                .code(this.code)
                .message(this.message)
                .build();
    }
}