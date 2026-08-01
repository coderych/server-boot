package com.coderych.commons.core.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ResultCodeTests {

    @Test
    void allEnumValuesShouldHaveNonNullCode() {
        for (ResultCode rc : ResultCode.values()) {
            assertTrue(rc.getCode() != 0, "Code should be non-zero for " + rc.name());
        }
    }

    @Test
    void allEnumValuesShouldHaveNonNullMessage() {
        for (ResultCode rc : ResultCode.values()) {
            assertNotNull(rc.getMessage(), "Message should not be null for " + rc.name());
            assertFalse(rc.getMessage().isBlank(), "Message should not be blank for " + rc.name());
        }
    }

    @Test
    void successShouldHaveCode2000() {
        assertEquals(2000, ResultCode.SUCCESS.getCode());
        assertEquals("操作成功", ResultCode.SUCCESS.getMessage());
    }

    @Test
    void errorShouldHaveCode5001() {
        assertEquals(5001, ResultCode.ERROR.getCode());
        assertEquals("服务器内部错误", ResultCode.ERROR.getMessage());
    }

    @Test
    void badRequestShouldHaveCode4000() {
        assertEquals(4000, ResultCode.BAD_REQUEST.getCode());
    }

    @Test
    void unauthorizedShouldHaveCode4001() {
        assertEquals(4001, ResultCode.UNAUTHORIZED.getCode());
    }

    @Test
    void forbiddenShouldHaveCode4003() {
        assertEquals(4003, ResultCode.FORBIDDEN.getCode());
    }

    @Test
    void notFoundShouldHaveCode4004() {
        assertEquals(4004, ResultCode.NOT_FOUND.getCode());
    }

    @Test
    void valueOfShouldReturnCorrectEnum() {
        assertEquals(ResultCode.SUCCESS, ResultCode.valueOf("SUCCESS"));
        assertEquals(ResultCode.ERROR, ResultCode.valueOf("ERROR"));
    }
}
