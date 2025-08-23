package com.clody.domain.comment.exception;



import com.clody.global.apiPayload.code.BaseErrorCode;
import com.clody.global.apiPayload.code.ErrorReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CommentErrorCode implements BaseErrorCode {

    NOT_FOUND(HttpStatus.NOT_FOUND, "COMMENT_NOT_FOUND", "댓글을 찾을 수 없습니다."),
    PARENT_NOT_FOUND(HttpStatus.NOT_FOUND, "PARENT_COMMENT_NOT_FOUND", "부모 댓글이 존재하지 않습니다."),
    PARENT_MISMATCH(HttpStatus.BAD_REQUEST, "PARENT_COMMENT_MISMATCH", "부모 댓글이 해당 OOTD에 속하지 않습니다."),
    FORBIDDEN_NOT_AUTHOR(HttpStatus.FORBIDDEN, "COMMENT_NOT_AUTHOR", "댓글 작성자가 아닙니다."),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND,"MEMBER_NOT_FOUND","해당 멤버를 찾을 수 없습니다.");
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
