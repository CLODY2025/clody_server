package com.clody.domain.hashtag.exception;


import com.clody.global.apiPayload.exception.GeneralException;

public class HashtagException extends GeneralException {
    public HashtagException(HashtagErrorCode code) {
        super(code);
    }
}
