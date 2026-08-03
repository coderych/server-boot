package com.coderych.commons.job.support;

import com.coderych.commons.job.model.JobRetryPolicy;
import com.coderych.commons.job.model.JobScheduleRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.quartz.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Quartz Job 数据映射工具类。
 * <p>
 * 负责在 {@link JobScheduleRequest}、{@link JobRetryPolicy} 与 Quartz 原生对象
 * （{@link JobDetail}、{@link Trigger}、{@link JobDataMap}）之间进行转换，
 * 同时管理重试触发器的键名生成。
 *
 * @author YCH
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class JobDataMapper {

    /**
     * 处理器名称数据键。
     */
    public static final String HANDLER_NAME = "commons.job.handlerName";

    /**
     * 任务数据键。
     */
    public static final String JOB_DATA = "commons.job.data";

    /**
     * 人工触发标识键。
     */
    public static final String MANUAL_TRIGGER = "commons.job.manualTrigger";

    /**
     * 执行次数数据键。
     */
    public static final String ATTEMPT = "commons.job.attempt";

    /**
     * 最大重试次数数据键。
     */
    public static final String RETRY_MAX_ATTEMPTS = "commons.job.retry.maxAttempts";

    /**
     * 重试延迟数据键。
     */
    public static final String RETRY_DELAY_MS = "commons.job.retry.delayMs";

    /**
     * 重试退避乘数数据键。
     */
    public static final String RETRY_BACKOFF_MULTIPLIER = "commons.job.retry.backoffMultiplier";

    /**
     * 触发器名称后缀。
     */
    public static final String TRIGGER_SUFFIX = "-trigger";

    /**
     * 重试触发器名称后缀。
     */
    public static final String RETRY_TRIGGER_SUFFIX = "-retry-trigger";

    public static JobDetail toJobDetail(JobScheduleRequest request, JobRetryPolicy retryPolicy) {
        return JobBuilder.newJob(DelegatingQuartzJob.class)
                .withIdentity(jobKey(request))
                .usingJobData(toJobDataMap(request, retryPolicy))
                .storeDurably()
                .build();
    }

    public static Trigger toTrigger(JobScheduleRequest request) {
        TriggerBuilder<Trigger> builder = TriggerBuilder.newTrigger()
                .withIdentity(triggerKey(request.getJobName(), request.getJobGroup()))
                .forJob(jobKey(request));
        if (request.isCron()) {
            return builder.withSchedule(CronScheduleBuilder.cronSchedule(request.getCronExpression())).build();
        }
        return builder.startAt(java.util.Date.from(request.getStartAt())).build();
    }

    public static Trigger toRetryTrigger(JobExecutionContext context, int nextAttempt, long delayMs) {
        JobKey jobKey = context.getJobDetail().getKey();
        JobDataMap dataMap = new JobDataMap();
        dataMap.put(ATTEMPT, nextAttempt);
        dataMap.put(MANUAL_TRIGGER, false);

        return TriggerBuilder.newTrigger()
                .withIdentity(retryTriggerKey(jobKey.getName(), jobKey.getGroup()))
                .forJob(jobKey)
                .usingJobData(dataMap)
                .startAt(new java.util.Date(System.currentTimeMillis() + delayMs))
                .build();
    }

    public static JobDataMap toJobDataMap(JobScheduleRequest request, JobRetryPolicy retryPolicy) {
        JobDataMap dataMap = new JobDataMap();
        dataMap.put(HANDLER_NAME, request.getHandlerName());
        dataMap.put(JOB_DATA, new HashMap<>(request.getData()));
        dataMap.put(MANUAL_TRIGGER, false);
        dataMap.put(ATTEMPT, 1);
        dataMap.put(RETRY_MAX_ATTEMPTS, retryPolicy.getMaxAttempts());
        dataMap.put(RETRY_DELAY_MS, retryPolicy.getDelay().toMillis());
        dataMap.put(RETRY_BACKOFF_MULTIPLIER, retryPolicy.getBackoffMultiplier());
        return dataMap;
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> readData(JobDataMap dataMap) {
        Object data = dataMap.get(JOB_DATA);
        if (data instanceof Map<?, ?> map) {
            return (Map<String, Object>) map;
        }
        return Map.of();
    }

    public static JobKey jobKey(JobScheduleRequest request) {
        return jobKey(request.getJobName(), request.getJobGroup());
    }

    public static JobKey jobKey(String jobName, String jobGroup) {
        return JobKey.jobKey(jobName, jobGroup);
    }

    public static TriggerKey triggerKey(String jobName, String jobGroup) {
        return TriggerKey.triggerKey(jobName + TRIGGER_SUFFIX, jobGroup);
    }

    public static TriggerKey retryTriggerKey(String jobName, String jobGroup) {
        return TriggerKey.triggerKey(jobName + RETRY_TRIGGER_SUFFIX, jobGroup);
    }
}
