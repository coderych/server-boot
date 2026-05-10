package com.coderych.commons.job.model;

import lombok.Builder;
import lombok.Getter;
import org.quartz.JobExecutionContext;

import java.util.Map;

/**
 * 任务执行上下文，封装单次任务执行所需的全部信息。
 * <p>
 * 包含调度器元数据（Job/Trigger 标识）、业务数据、重试状态以及原始 Quartz 上下文。
 *
 * @author YCH
 */
@Getter
@Builder
public class JobExecuteContext {

    private final String handlerName;

    private final String jobName;

    private final String jobGroup;

    private final String triggerName;

    private final String triggerGroup;

    private final Map<String, Object> data;

    private final boolean manualTrigger;

    private final int attempt;

    private final int maxAttempts;

    private final JobExecutionContext quartzContext;
}
