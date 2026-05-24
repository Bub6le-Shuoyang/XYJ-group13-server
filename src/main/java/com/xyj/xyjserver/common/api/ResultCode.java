package com.xyj.xyjserver.common.api;

/**
 * 状态码枚举
 */
public enum ResultCode {
    SUCCESS(200, "success"),
    UNAUTHORIZED(401, "未授权或Token已过期"),
    FORBIDDEN(403, "没有访问权限"),
    VALIDATE_FAILED(400, "参数校验失败"),
    FAILED(500, "操作失败");

    private final int code;
    private final String message;

    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}