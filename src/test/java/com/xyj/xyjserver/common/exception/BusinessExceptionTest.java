package com.xyj.xyjserver.common.exception;

import com.xyj.xyjserver.common.api.ResultCode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BusinessExceptionTest {

    @Test
    void constructor_withResultCode_shouldUseEnumMessage() {
        BusinessException ex = new BusinessException(ResultCode.FORBIDDEN);

        assertEquals(ResultCode.FORBIDDEN, ex.getErrorCode());
        assertEquals(ResultCode.FORBIDDEN.getMessage(), ex.getMessage());
    }

    @Test
    void constructor_withMessage_shouldDefaultToFailedCode() {
        BusinessException ex = new BusinessException("业务失败");

        assertEquals(ResultCode.FAILED, ex.getErrorCode());
        assertEquals("业务失败", ex.getMessage());
    }

    @Test
    void constructor_withResultCodeAndMessage_shouldKeepBoth() {
        BusinessException ex = new BusinessException(ResultCode.VALIDATE_FAILED, "邮箱格式错误");

        assertEquals(ResultCode.VALIDATE_FAILED, ex.getErrorCode());
        assertEquals("邮箱格式错误", ex.getMessage());
    }
}
