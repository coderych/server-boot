package com.coderych.commons.job.util;

import com.coderych.commons.job.model.JobRetryPolicy;
import com.coderych.commons.job.model.JobScheduleRequest;
import com.coderych.commons.job.support.JobDataMapper;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.quartz.*;
import org.springframework.util.Assert;

import java.util.Map;

/**
 * 基于 Quartz 的任务管理器静态工具类。
 * <p>
 * 提供任务的调度、重新调度、手动触发、暂停、恢复和删除等功能，
 * 内置重试策略支持和 Scheduler 异常统一包装。需通过 {@link #init} 方法初始化后使用。
 *
 * @author YCH
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class QuartzJobManager {

    private static volatile Scheduler scheduler;

    private static volatile JobRetryPolicy defaultRetryPolicy;

    public static void init(Scheduler scheduler, JobRetryPolicy defaultRetryPolicy) {
        Assert.notNull(scheduler, "Scheduler must not be null");
        Assert.notNull(defaultRetryPolicy, "JobRetryPolicy must not be null");
        QuartzJobManager.scheduler = scheduler;
        QuartzJobManager.defaultRetryPolicy = defaultRetryPolicy;
    }

    public static void schedule(JobScheduleRequest request) {
        validateRequest(request);
        withScheduler(() -> {
            JobKey jobKey = JobDataMapper.jobKey(request);
            if (scheduler.checkExists(jobKey)) {
                throw new IllegalStateException("Job already exists: " + jobKey);
            }
            JobRetryPolicy retryPolicy = effectiveRetryPolicy(request.getRetryPolicy());
            scheduler.scheduleJob(
                    JobDataMapper.toJobDetail(request, retryPolicy),
                    JobDataMapper.toTrigger(request)
            );
            triggerImmediatelyIfNecessary(request);
        }, "Failed to schedule job");
    }

    public static void reschedule(JobScheduleRequest request) {
        validateRequest(request);
        withScheduler(() -> {
            JobRetryPolicy retryPolicy = effectiveRetryPolicy(request.getRetryPolicy());
            JobKey jobKey = JobDataMapper.jobKey(request);
            if (!scheduler.checkExists(jobKey)) {
                scheduler.scheduleJob(
                        JobDataMapper.toJobDetail(request, retryPolicy),
                        JobDataMapper.toTrigger(request)
                );
                return;
            }

            scheduler.addJob(JobDataMapper.toJobDetail(request, retryPolicy), true, true);
            scheduler.rescheduleJob(
                    JobDataMapper.triggerKey(request.getJobName(), request.getJobGroup()),
                    JobDataMapper.toTrigger(request)
            );
            deleteRetryTrigger(request.getJobName(), request.getJobGroup());
            triggerImmediatelyIfNecessary(request);
        }, "Failed to reschedule job");
    }

    public static void trigger(String jobName, String jobGroup) {
        trigger(jobName, jobGroup, Map.of());
    }

    public static void trigger(String jobName, String jobGroup, Map<String, Object> data) {
        withScheduler(() -> {
            JobDataMap triggerData = new JobDataMap();
            triggerData.put(JobDataMapper.MANUAL_TRIGGER, true);
            if (data != null && !data.isEmpty()) {
                triggerData.put(JobDataMapper.JOB_DATA, data);
            }
            scheduler.triggerJob(JobDataMapper.jobKey(jobName, jobGroup), triggerData);
        }, "Failed to trigger job");
    }

    public static void pause(String jobName, String jobGroup) {
        withScheduler(() -> scheduler.pauseJob(JobDataMapper.jobKey(jobName, jobGroup)), "Failed to pause job");
    }

    public static void resume(String jobName, String jobGroup) {
        withScheduler(() -> scheduler.resumeJob(JobDataMapper.jobKey(jobName, jobGroup)), "Failed to resume job");
    }

    public static boolean delete(String jobName, String jobGroup) {
        return withScheduler(() -> scheduler.deleteJob(JobDataMapper.jobKey(jobName, jobGroup)), "Failed to delete job");
    }

    public static boolean exists(String jobName, String jobGroup) {
        return withScheduler(() -> scheduler.checkExists(JobDataMapper.jobKey(jobName, jobGroup)), "Failed to check job existence");
    }

    public static Trigger.TriggerState getTriggerState(String jobName, String jobGroup) {
        return withScheduler(() -> scheduler.getTriggerState(JobDataMapper.triggerKey(jobName, jobGroup)), "Failed to get trigger state");
    }

    private static JobRetryPolicy effectiveRetryPolicy(JobRetryPolicy retryPolicy) {
        return retryPolicy == null ? defaultRetryPolicy : retryPolicy;
    }

    /**
     * 校验调度请求参数，cronExpression 和 startAt 必须且只能提供其一。
     */
    private static void validateRequest(JobScheduleRequest request) {
        Assert.notNull(request, "request must not be null");
        Assert.hasText(request.getHandlerName(), "handlerName must not be blank");
        Assert.hasText(request.getJobName(), "jobName must not be blank");
        Assert.hasText(request.getJobGroup(), "jobGroup must not be blank");
        boolean hasCron = request.getCronExpression() != null && !request.getCronExpression().isBlank();
        boolean hasStartAt = request.getStartAt() != null;
        Assert.isTrue(hasCron != hasStartAt, "Exactly one of cronExpression or startAt must be provided");
    }

    private static void triggerImmediatelyIfNecessary(JobScheduleRequest request) {
        if (request.isImmediate()) {
            trigger(request.getJobName(), request.getJobGroup(), request.getData());
        }
    }

    private static void deleteRetryTrigger(String jobName, String jobGroup) throws SchedulerException {
        TriggerKey retryTriggerKey = JobDataMapper.retryTriggerKey(jobName, jobGroup);
        scheduler.unscheduleJob(retryTriggerKey);
    }

    private static void withScheduler(SchedulerAction action, String errorMsg) {
        try {
            action.execute();
        } catch (SchedulerException exception) {
            throw new IllegalStateException(errorMsg, exception);
        }
    }

    private static <T> T withScheduler(SchedulerSupplier<T> action, String errorMsg) {
        try {
            return action.get();
        } catch (SchedulerException exception) {
            throw new IllegalStateException(errorMsg, exception);
        }
    }

    @FunctionalInterface
    private interface SchedulerAction {
        void execute() throws SchedulerException;
    }

    @FunctionalInterface
    private interface SchedulerSupplier<T> {
        T get() throws SchedulerException;
    }
}
