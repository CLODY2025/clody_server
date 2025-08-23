package com.clody.domain.comment.exception;


import com.clody.global.apiPayload.exception.GeneralException;

public class CommentException extends GeneralException {
    public CommentException(CommentErrorCode code) {
        super(code);
    }
}
