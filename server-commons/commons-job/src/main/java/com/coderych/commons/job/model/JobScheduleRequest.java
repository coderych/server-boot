package com.coderych.commons.job.model;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.Instant;
import java.util.Map;

/**
 * 任务调度请求，描述一个待调度或待重新调度的任务。
 * <p>
 * 支持 Cron 表达式和指定起始时间两种触发模式，可配置重试策略和立即执行标志。
 *
 * @author YCH
 */
@Getter
@ToString
@Builder
@Accessors(fluent = false)
public class JobScheduleRequest {

    /**
     * 任务处理器名称。
     */
    private final String handlerName;

    private final String jobName;

    /**
     * 任务分组。
     */
    @Builder.Default
    private final String jobGroup = "DEFAULT";

    /**
     * Cron 表达式。
     */
    private final String cronExpression;

    /**
     * 一次性任务的开始时间。
     */
    private final Instant startAt;

    /**
     * 任务执行数据。
     */
    @Builder.Default
    private final Map<String, Object> data = Map.of();

    /**
     * 是否允许同一任务并发执行。
     */
    @Builder.Default
    private final boolean concurrent = false;

    /**
     * Cron misfire 策略：1 立即执行，2 忽略错过的触发，3 放弃错过的触发。
     */
    @Builder.Default
    private final int misfirePolicy = 3;

    /**
     * 任务重试策略。
     */
    private final JobRetryPolicy retryPolicy;

    /**
     * 是否创建后立即触发。
     */
    @Builder.Default
    private final boolean immediate = false;

    public boolean isCron() {
        return cronExpression != null;
    }

}
