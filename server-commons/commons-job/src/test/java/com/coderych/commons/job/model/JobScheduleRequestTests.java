package com.coderych.commons.job.model;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class JobScheduleRequestTests {

    @Test
    void shouldReturnTrueWhenCronExpressionIsPresent() {
        JobScheduleRequest request = JobScheduleRequest.builder()
                .handlerName("testHandler")
                .jobName("testJob")
                .cronExpression("0 0/5 * * * ?")
                .build();

        assertTrue(request.isCron());
    }

    @Test
    void shouldReturnFalseWhenCronExpressionIsNull() {
        JobScheduleRequest request = JobScheduleRequest.builder()
                .handlerName("testHandler")
                .jobName("testJob")
                .startAt(Instant.now())
                .build();

        assertFalse(request.isCron());
    }

    @Test
    void shouldDefaultJobGroupToDefault() {
        JobScheduleRequest request = JobScheduleRequest.builder()
                .handlerName("testHandler")
                .jobName("testJob")
                .cronExpression("0 0/5 * * * ?")
                .build();

        assertEquals("DEFAULT", request.getJobGroup());
    }

    @Test
    void shouldDefaultImmediateToFalse() {
        JobScheduleRequest request = JobScheduleRequest.builder()
                .handlerName("testHandler")
                .jobName("testJob")
                .cronExpression("0 0/5 * * * ?")
                .build();

        assertFalse(request.isImmediate());
    }

    @Test
    void shouldSetAllFieldsCorrectly() {
        Instant startAt = Instant.now();
        Map<String, Object> data = Map.of("key1", "value1", "key2", 42);
        JobRetryPolicy retryPolicy = JobRetryPolicy.builder()
                .maxAttempts(3)
                .delay(java.time.Duration.ofSeconds(1))
                .backoffMultiplier(2.0)
                .build();

        JobScheduleRequest request = JobScheduleRequest.builder()
                .handlerName("myHandler")
                .jobName("myJob")
                .jobGroup("myGroup")
                .startAt(startAt)
                .data(data)
                .retryPolicy(retryPolicy)
                .immediate(true)
                .build();

        assertEquals("myHandler", request.getHandlerName());
        assertEquals("myJob", request.getJobName());
        assertEquals("myGroup", request.getJobGroup());
        assertEquals(startAt, request.getStartAt());
        assertEquals(data, request.getData());
        assertEquals(retryPolicy, request.getRetryPolicy());
        assertTrue(request.isImmediate());
        assertFalse(request.isCron());
    }

    @Test
    void shouldHandleNullData() {
        JobScheduleRequest request = JobScheduleRequest.builder()
                .handlerName("testHandler")
                .jobName("testJob")
                .cronExpression("0 0/5 * * * ?")
                .build();

        assertNull(request.getData());
    }

    @Test
    void shouldHandleNullRetryPolicy() {
        JobScheduleRequest request = JobScheduleRequest.builder()
                .handlerName("testHandler")
                .jobName("testJob")
                .cronExpression("0 0/5 * * * ?")
                .build();

        assertNull(request.getRetryPolicy());
    }
}
