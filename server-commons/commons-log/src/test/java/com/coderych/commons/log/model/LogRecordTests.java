package com.coderych.commons.log.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LogRecordTests {

    @Test
    void shouldBuildWithAllFields() {
        LogRecord record = LogRecord.builder()
                .traceId("trace-123")
                .requestMethod("GET")
                .requestUri("/api/users")
                .ipAddress("127.0.0.1")
                .userAgent("Mozilla/5.0")
                .controllerClass("com.example.UserController")
                .controllerMethod("getUser")
                .userId("1")
                .username("admin")
                .success(true)
                .duration(100L)
                .requestBody("{\"name\":\"test\"}")
                .responseBody("{\"id\":1}")
                .exceptionName(null)
                .exceptionMessage(null)
                .timestamp(1700000000000L)
                .build();

        assertEquals("trace-123", record.getTraceId());
        assertEquals("GET", record.getRequestMethod());
        assertEquals("/api/users", record.getRequestUri());
        assertEquals("127.0.0.1", record.getIpAddress());
        assertEquals("Mozilla/5.0", record.getUserAgent());
        assertEquals("com.example.UserController", record.getControllerClass());
        assertEquals("getUser", record.getControllerMethod());
        assertEquals("1", record.getUserId());
        assertEquals("admin", record.getUsername());
        assertTrue(record.isSuccess());
        assertEquals(100L, record.getDuration());
        assertEquals("{\"name\":\"test\"}", record.getRequestBody());
        assertEquals("{\"id\":1}", record.getResponseBody());
        assertNull(record.getExceptionName());
        assertNull(record.getExceptionMessage());
        assertEquals(1700000000000L, record.getTimestamp());
    }

    @Test
    void shouldBuildWithDefaultSuccessFalse() {
        LogRecord record = LogRecord.builder()
                .success(false)
                .build();

        assertFalse(record.isSuccess());
    }

    @Test
    void shouldBuildWithExceptionInfo() {
        LogRecord record = LogRecord.builder()
                .success(false)
                .exceptionName("java.lang.RuntimeException")
                .exceptionMessage("something went wrong")
                .build();

        assertFalse(record.isSuccess());
        assertEquals("java.lang.RuntimeException", record.getExceptionName());
        assertEquals("something went wrong", record.getExceptionMessage());
    }

    @Test
    void shouldHaveNullFieldsWhenNotSet() {
        LogRecord record = LogRecord.builder().build();

        assertNull(record.getTraceId());
        assertNull(record.getRequestMethod());
        assertNull(record.getRequestUri());
        assertNull(record.getIpAddress());
        assertNull(record.getUserAgent());
        assertNull(record.getControllerClass());
        assertNull(record.getControllerMethod());
        assertNull(record.getUserId());
        assertNull(record.getUsername());
        assertNull(record.getRequestBody());
        assertNull(record.getResponseBody());
        assertNull(record.getExceptionName());
        assertNull(record.getExceptionMessage());
    }

    @Test
    void shouldHaveZeroDurationAndTimestampByDefault() {
        LogRecord record = LogRecord.builder().build();

        assertEquals(0L, record.getDuration());
        assertEquals(0L, record.getTimestamp());
    }
}
