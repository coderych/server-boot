package com.coderych.commons.core.exception;

import com.coderych.commons.core.enums.ResultCode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class BizExceptionTests {

    @Test
    void shouldUseResultCodeMessageAndCode() {
        BizException exception = new BizException(ResultCode.PERMISSION_DENIED);

        assertEquals(ResultCode.PERMISSION_DENIED.getCode(), exception.getCode());
        assertEquals(ResultCode.PERMISSION_DENIED.getMessage(), exception.getMessage());
    }

    @Test
    void shouldKeepCustomMessageAndCause() {
        RuntimeException cause = new RuntimeException("boom");
        BizException exception = new BizException(ResultCode.INTERNAL_ERROR, "custom", cause);

        assertEquals(ResultCode.INTERNAL_ERROR.getCode(), exception.getCode());
        assertEquals("custom", exception.getMessage());
        assertSame(cause, exception.getCause());
    }
}
