package com.coderych.commons.job.model;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JobRetryPolicyTests {

    @Test
    void shouldCreatePolicyWithValidParameters() {
        JobRetryPolicy policy = new JobRetryPolicy(3, Duration.ofSeconds(1), 2.0);

        assertEquals(3, policy.getMaxAttempts());
        assertEquals(Duration.ofSeconds(1), policy.getDelay());
        assertEquals(2.0, policy.getBackoffMultiplier());
    }

    @Test
    void shouldThrowWhenMaxAttemptsLessThan1() {
        assertThrows(IllegalArgumentException.class,
                () -> new JobRetryPolicy(0, Duration.ofSeconds(1), 2.0));
    }

    @Test
    void shouldThrowWhenMaxAttemptsIsNegative() {
        assertThrows(IllegalArgumentException.class,
                () -> new JobRetryPolicy(-1, Duration.ofSeconds(1), 2.0));
    }

    @Test
    void shouldThrowWhenDelayIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> new JobRetryPolicy(3, null, 2.0));
    }

    @Test
    void shouldThrowWhenDelayIsNegative() {
        assertThrows(IllegalArgumentException.class,
                () -> new JobRetryPolicy(3, Duration.ofSeconds(-1), 2.0));
    }

    @Test
    void shouldThrowWhenBackoffMultiplierLessThan1() {
        assertThrows(IllegalArgumentException.class,
                () -> new JobRetryPolicy(3, Duration.ofSeconds(1), 0.5));
    }

    @Test
    void shouldAcceptBackoffMultiplierEqualTo1() {
        JobRetryPolicy policy = new JobRetryPolicy(3, Duration.ofSeconds(1), 1.0);

        assertEquals(1.0, policy.getBackoffMultiplier());
    }

    @Test
    void shouldAcceptMaxAttemptsEqualTo1() {
        JobRetryPolicy policy = new JobRetryPolicy(1, Duration.ofSeconds(1), 2.0);

        assertEquals(1, policy.getMaxAttempts());
    }

    @Test
    void shouldAcceptZeroDelay() {
        JobRetryPolicy policy = new JobRetryPolicy(3, Duration.ZERO, 2.0);

        assertEquals(Duration.ZERO, policy.getDelay());
    }

    @Test
    void shouldBuildUsingBuilder() {
        JobRetryPolicy policy = JobRetryPolicy.builder()
                .maxAttempts(5)
                .delay(Duration.ofMillis(500))
                .backoffMultiplier(1.5)
                .build();

        assertEquals(5, policy.getMaxAttempts());
        assertEquals(Duration.ofMillis(500), policy.getDelay());
        assertEquals(1.5, policy.getBackoffMultiplier());
    }
}
