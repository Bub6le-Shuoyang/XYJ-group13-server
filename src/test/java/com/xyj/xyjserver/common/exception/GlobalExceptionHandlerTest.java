package com.xyj.xyjserver.common.exception;

import com.xyj.xyjserver.common.api.Result;
import com.xyj.xyjserver.common.api.ResultCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void handleBusinessException_shouldReturnFailedResult() {
        BusinessException ex = new BusinessException(ResultCode.FORBIDDEN, "无权访问");

        Result<Object> result = handler.handleBusinessException(ex);

        assertEquals(ResultCode.FORBIDDEN.getCode(), result.getCode());
        assertEquals("无权访问", result.getMessage());
    }

    @Test
    void handleValidException_shouldReturnFieldErrorMessage() throws NoSuchMethodException {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "target");
        bindingResult.addError(new FieldError("target", "email", "不能为空"));
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);

        Result<Object> result = handler.handleValidException(ex);

        assertEquals(ResultCode.VALIDATE_FAILED.getCode(), result.getCode());
        assertEquals("email不能为空", result.getMessage());
    }

    @Test
    void handleNoResourceFound_shouldReturn404() {
        NoResourceFoundException ex = mock(NoResourceFoundException.class);

        ResponseEntity<Void> response = handler.handleNoResourceFound(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void handleException_shouldReturnGenericError() {
        Result<Object> result = handler.handleException(new RuntimeException("boom"));

        assertEquals(ResultCode.FAILED.getCode(), result.getCode());
        assertEquals("系统内部错误", result.getMessage());
    }
}
