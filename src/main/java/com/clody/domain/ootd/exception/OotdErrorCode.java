package com.clody.domain.ootd.exception;



import com.clody.global.apiPayload.code.BaseErrorCode;
import com.clody.global.apiPayload.code.ErrorReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum OotdErrorCode implements BaseErrorCode {

    NOT_FOUND(HttpStatus.NOT_FOUND, "OOTD404", "해당 OOTD를 찾을 수 없습니다."),
    IO_FAIL(HttpStatus.BAD_REQUEST, "FILE400", "입력 파일이 문제가 있습니다."),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER404", "하드코딩된 멤버를 찾을 수 없습니다.")
    ;

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
