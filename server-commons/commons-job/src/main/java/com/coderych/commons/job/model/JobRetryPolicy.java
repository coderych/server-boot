package com.coderych.commons.job.model;

import lombok.Builder;
import lombok.Getter;

import java.time.Duration;

/**
 * 任务重试策略，定义最大重试次数、延迟时间和退避乘数。
 * <p>
 * 退避算法：第 n 次重试延迟 = delay * backoffMultiplier^(n-1)。
 *
 * @author YCH
 */
@Getter
@Builder
public class JobRetryPolicy {

    private final int maxAttempts;

    private final Duration delay;

    private final double backoffMultiplier;

    public JobRetryPolicy(int maxAttempts, Duration delay, double backoffMultiplier) {
        if (maxAttempts < 1) {
            throw new IllegalArgumentException("maxAttempts must be greater than 0");
        }
        if (delay == null || delay.isNegative()) {
            throw new IllegalArgumentException("delay must not be null or negative");
        }
        if (backoffMultiplier < 1.0D) {
            throw new IllegalArgumentException("backoffMultiplier must be greater than or equal to 1");
        }
        this.maxAttempts = maxAttempts;
        this.delay = delay;
        this.backoffMultiplier = backoffMultiplier;
    }
}
