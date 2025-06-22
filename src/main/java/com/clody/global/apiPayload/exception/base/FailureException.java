package com.clody.global.apiPayload.exception.base;

import com.clody.global.apiPayload.code.BaseErrorCode;
import com.clody.global.apiPayload.exception.GeneralException;

public class FailureException extends GeneralException {

    public FailureException(BaseErrorCode baseErrorCode) {
        super(baseErrorCode);
    }
}
