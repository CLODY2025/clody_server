package com.clody.domain.member.exception;

import com.clody.global.apiPayload.code.BaseErrorCode;
import com.clody.global.apiPayload.code.ErrorReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum MemberErrorCode implements BaseErrorCode {

    // Member 관련 에러
    NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER404", "회원을 찾을 수 없습니다."),
    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "MEMBER4001", "이미 존재하는 이메일입니다."),
    NICKNAME_ALREADY_EXISTS(HttpStatus.CONFLICT, "MEMBER4002", "이미 존재하는 닉네임입니다."),
    EMAIL_NOT_VERIFIED(HttpStatus.BAD_REQUEST, "MEMBER4003", "이메일 인증이 완료되지 않았습니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "MEMBER4004", "비밀번호가 올바르지 않습니다."),
    SAME_ACCOUNT_SCOPE(HttpStatus.BAD_REQUEST, "MEMBER4005", "현재와 동일한 계정 범위입니다."),
    SAME_PASSWORD(HttpStatus.BAD_REQUEST, "MEMBER4006", "현재 비밀번호와 동일합니다."),
    INVALID_IMAGE_FILE(HttpStatus.BAD_REQUEST, "MEMBER4007", "유효하지 않은 이미지 파일입니다."),
    IMAGE_FILE_TOO_LARGE(HttpStatus.BAD_REQUEST, "MEMBER4008", "이미지 파일 크기가 너무 큽니다. (최대 5MB)"),
    INVALID_IMAGE_FORMAT(HttpStatus.BAD_REQUEST, "MEMBER4009", "지원하지 않는 이미지 형식입니다. (jpg, jpeg, png, gif, webp만 허용)"),
    SAME_NICKNAME(HttpStatus.BAD_REQUEST, "MEMBER4010", "현재와 동일한 닉네임입니다."),
    
    // Email Verification 관련 에러
    EMAIL_VERIFICATION_CODE_INVALID(HttpStatus.BAD_REQUEST, "EMAIL4001", "인증번호가 올바르지 않습니다."),
    EMAIL_VERIFICATION_CODE_EXPIRED(HttpStatus.BAD_REQUEST, "EMAIL4002", "인증번호가 만료되었습니다."),
    EMAIL_VERIFICATION_FAILED(HttpStatus.BAD_REQUEST, "EMAIL4003", "이메일 인증에 실패했습니다."),
    EMAIL_SEND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "EMAIL5001", "이메일 발송에 실패했습니다.");

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