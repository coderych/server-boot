package com.coderych.commons.core.util;

import com.coderych.commons.core.enums.ResultCode;
import com.coderych.commons.core.exception.BadRequestException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JSONTests {

    @Test
    void shouldWrapParseFailureWithBadRequestException() {
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> JSON.parseObject("{\"name\":", Demo.class));

        assertEquals(ResultCode.BAD_REQUEST.getCode(), exception.getCode());
        assertNotNull(exception.getCause());
    }

    @Test
    void shouldWrapConvertFailureWithBadRequestException() {
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> JSON.convert("oops", Integer.class));

        assertEquals(ResultCode.BAD_REQUEST.getCode(), exception.getCode());
        assertNotNull(exception.getCause());
    }

    record Demo(String name) {
    }
}
