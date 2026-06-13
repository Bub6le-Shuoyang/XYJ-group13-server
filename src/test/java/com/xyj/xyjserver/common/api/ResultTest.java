package com.xyj.xyjserver.common.api;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ResultTest {

    @Test
    void success_withoutData_shouldUseSuccessCode() {
        Result<Void> result = Result.success();

        assertEquals(ResultCode.SUCCESS.getCode(), result.getCode());
        assertEquals(ResultCode.SUCCESS.getMessage(), result.getMessage());
        assertNull(result.getData());
        assertNotNull(result.getTimestamp());
    }

    @Test
    void success_withData_shouldWrapPayload() {
        Result<String> result = Result.success("hello");

        assertEquals(200, result.getCode());
        assertEquals("hello", result.getData());
    }

    @Test
    void success_withCustomMessage_shouldOverrideDefaultMessage() {
        Result<Integer> result = Result.success(1, "操作完成");

        assertEquals(200, result.getCode());
        assertEquals("操作完成", result.getMessage());
        assertEquals(1, result.getData());
    }

    @Test
    void failed_shouldMapErrorCodes() {
        Result<Object> validateFailed = Result.failed(ResultCode.VALIDATE_FAILED, "邮箱格式错误");
        assertEquals(400, validateFailed.getCode());
        assertEquals("邮箱格式错误", validateFailed.getMessage());

        Result<Object> genericFailed = Result.failed("系统繁忙");
        assertEquals(500, genericFailed.getCode());
        assertEquals("系统繁忙", genericFailed.getMessage());

        Result<Object> defaultFailed = Result.failed();
        assertEquals(ResultCode.FAILED.getCode(), defaultFailed.getCode());
    }
}
