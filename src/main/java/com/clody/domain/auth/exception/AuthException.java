package com.clody.domain.auth.exception;

import com.clody.global.apiPayload.exception.GeneralException;

public class AuthException extends GeneralException {
    public AuthException(AuthErrorCode code) {
        super(code);
    }
}