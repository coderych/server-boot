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

    /**
     * 任务处理器名称。
     */
    private final String handlerName;

    private final String jobName;

    /**
     * 任务分组。
     */
    private final String jobGroup;

    /**
     * 触发器名称。
     */
    private final String triggerName;

    /**
     * 触发器分组。
     */
    private final String triggerGroup;

    /**
     * 任务执行数据。
     */
    private final Map<String, Object> data;

    /**
     * 是否由人工触发。
     */
    private final boolean manualTrigger;

    /**
     * 当前执行次数。
     */
    private final int attempt;

    /**
     * 最大执行次数。
     */
    private final int maxAttempts;

    /**
     * 原始 Quartz 执行上下文。
     */
    private final JobExecutionContext quartzContext;
}
