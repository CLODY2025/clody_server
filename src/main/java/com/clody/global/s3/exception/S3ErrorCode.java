package com.clody.global.s3.exception;



import com.clody.global.apiPayload.code.BaseErrorCode;
import com.clody.global.apiPayload.code.ErrorReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum S3ErrorCode implements BaseErrorCode {

    NOT_FOUND(HttpStatus.NOT_FOUND, "FILE404", "해당 파일을 찾을 수 없습니다."),
    UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "UPLOAD0500", "업로드 처리 중 오류가 발생했습니다."),
    DOWNLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "DOWNLOAD500", "다운로드 처리 중 오류가 발생했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    @Override
    public ErrorReasonDTO getReasonHttpStatus() {
        return ErrorReasonDTO.builder()
                .httpStatus(this.status)
                .isSuccess(false) // 에러이므로 항상 false
                .code(this.code)
                .message(this.message)
                .build();
    }
}
