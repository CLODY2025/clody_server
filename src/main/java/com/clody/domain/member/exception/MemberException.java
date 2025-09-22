package com.clody.domain.member.exception;

import com.clody.global.apiPayload.exception.GeneralException;

public class MemberException extends GeneralException {
    public MemberException(MemberErrorCode code) {
        super(code);
    }
}