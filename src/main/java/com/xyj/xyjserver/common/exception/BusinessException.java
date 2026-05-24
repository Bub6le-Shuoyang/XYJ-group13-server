package com.xyj.xyjserver.common.exception;

import com.xyj.xyjserver.common.api.ResultCode;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    private final ResultCode errorCode;

    public BusinessException(ResultCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public BusinessException(String message) {
        super(message);
        this.errorCode = ResultCode.FAILED;
    }

    public BusinessException(ResultCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}