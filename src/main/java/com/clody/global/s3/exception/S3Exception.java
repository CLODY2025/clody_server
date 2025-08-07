package com.clody.global.s3.exception;


import com.clody.global.apiPayload.exception.GeneralException;

public class S3Exception extends GeneralException {
    public S3Exception(S3ErrorCode code) {
        super(code);
    }
}
