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

    private final String handlerName;

    private final String jobName;

    @Builder.Default
    private final String jobGroup = "DEFAULT";

    private final String cronExpression;

    private final Instant startAt;

    private final Map<String, Object> data;

    private final JobRetryPolicy retryPolicy;

    @Builder.Default
    private final boolean immediate = false;

    public boolean isCron() {
        return cronExpression != null;
    }

}
