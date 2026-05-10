package com.coderych.commons.core.model;

import com.coderych.commons.core.enums.ResultCode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RTests {

    @Test
    void okShouldReturnSuccessWithDefaultMessage() {
        R<?> r = R.ok();
        assertTrue(r.isSuccess());
        assertEquals(ResultCode.SUCCESS.getCode(), r.getCode());
        assertEquals(ResultCode.SUCCESS.getMessage(), r.getMessage());
        assertNull(r.getData());
        assertTrue(r.getTimestamp() > 0);
    }

    @Test
    void okWithDataShouldReturnSuccessWithData() {
        R<String> r = R.ok("hello");
        assertTrue(r.isSuccess());
        assertEquals(ResultCode.SUCCESS.getCode(), r.getCode());
        assertEquals("hello", r.getData());
    }

    @Test
    void okWithMessageAndDataShouldReturnSuccess() {
        R<Integer> r = R.ok("custom message", 42);
        assertTrue(r.isSuccess());
        assertEquals(ResultCode.SUCCESS.getCode(), r.getCode());
        assertEquals("custom message", r.getMessage());
        assertEquals(42, r.getData());
    }

    @Test
    void failShouldReturnErrorWithDefaultMessage() {
        R<?> r = R.fail();
        assertFalse(r.isSuccess());
        assertEquals(ResultCode.ERROR.getCode(), r.getCode());
        assertEquals(ResultCode.ERROR.getMessage(), r.getMessage());
        assertNull(r.getData());
    }

    @Test
    void failWithMessageShouldReturnError() {
        R<?> r = R.fail("something wrong");
        assertFalse(r.isSuccess());
        assertEquals(ResultCode.ERROR.getCode(), r.getCode());
        assertEquals("something wrong", r.getMessage());
    }

    @Test
    void failWithCodeAndMessageShouldReturnCustomError() {
        R<?> r = R.fail(404, "not found");
        assertFalse(r.isSuccess());
        assertEquals(404, r.getCode());
        assertEquals("not found", r.getMessage());
    }

    @Test
    void failWithResultCodeShouldReturnEnumError() {
        R<?> r = R.fail(ResultCode.BAD_REQUEST);
        assertFalse(r.isSuccess());
        assertEquals(ResultCode.BAD_REQUEST.getCode(), r.getCode());
        assertEquals(ResultCode.BAD_REQUEST.getMessage(), r.getMessage());
    }

    @Test
    void failWithResultCodeAndDataShouldReturnEnumErrorWithData() {
        R<?> r = R.fail(ResultCode.NOT_FOUND, "detail");
        assertFalse(r.isSuccess());
        assertEquals(ResultCode.NOT_FOUND.getCode(), r.getCode());
        assertEquals("detail", r.getData());
    }

    @Test
    void timestampShouldBeSetOnCreation() {
        long before = System.currentTimeMillis();
        R<?> r = R.ok();
        long after = System.currentTimeMillis();
        assertTrue(r.getTimestamp() >= before && r.getTimestamp() <= after);
    }

    @Test
    void successConstantsShouldMatchResultCode() {
        assertEquals(ResultCode.SUCCESS.getCode(), R.SUCCESS_CODE);
        assertEquals(ResultCode.SUCCESS.getMessage(), R.SUCCESS_MESSAGE);
        assertEquals(ResultCode.ERROR.getCode(), R.ERROR_CODE);
        assertEquals(ResultCode.ERROR.getMessage(), R.ERROR_MESSAGE);
    }
}
