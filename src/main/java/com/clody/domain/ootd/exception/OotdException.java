package com.clody.domain.ootd.exception;


import com.clody.global.apiPayload.exception.GeneralException;

public class OotdException extends GeneralException {
    public OotdException(OotdErrorCode code) {
        super(code);
    }
}
