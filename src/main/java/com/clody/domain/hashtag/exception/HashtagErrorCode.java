package com.clody.domain.hashtag.exception;



import com.clody.global.apiPayload.code.BaseErrorCode;
import com.clody.global.apiPayload.code.ErrorReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum HashtagErrorCode implements BaseErrorCode {

    TOO_MANY_HASHTAGS(HttpStatus.BAD_REQUEST, "HASHTAG400", "해시태그 허용개수를 초과했습니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "HASHTAG404", "지원하지 않는 해시태그입니다."),
    BLANK_EXIST(HttpStatus.BAD_REQUEST, "HASHTAG400", "비어있는 해시태그 항목이 존재합니다."),
    RETURN_FAIL(HttpStatus.BAD_REQUEST, "HASHTAG400", "해시태그 반환 실패"),
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
