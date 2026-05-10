package com.coderych.commons.job.support;

import com.coderych.commons.core.util.spring.SpringUtils;
import com.coderych.commons.job.model.JobExecuteContext;
import org.quartz.*;

import java.util.Map;

/**
 * Quartz {@link Job} 委托实现，将执行逻辑转发给 {@link JobHandler}。
 * <p>
 * 内置重试机制：执行失败时根据重试策略（退避延迟）自动调度下一次重试，
 * 执行成功后清理可能残留的重试触发器。
 *
 * @author YCH
 */
public class DelegatingQuartzJob implements Job {

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDataMap dataMap = context.getMergedJobDataMap();
        String handlerName = dataMap.getString(JobDataMapper.HANDLER_NAME);
        int attempt = dataMap.getIntValue(JobDataMapper.ATTEMPT);
        int maxAttempts = dataMap.getIntValue(JobDataMapper.RETRY_MAX_ATTEMPTS);
        long delayMs = dataMap.getLongValue(JobDataMapper.RETRY_DELAY_MS);
        double backoffMultiplier = dataMap.getDoubleValue(JobDataMapper.RETRY_BACKOFF_MULTIPLIER);
        boolean manualTrigger = dataMap.getBooleanValue(JobDataMapper.MANUAL_TRIGGER);
        Map<String, Object> data = JobDataMapper.readData(dataMap);

        JobExecuteContext executeContext = JobExecuteContext.builder()
                .handlerName(handlerName)
                .jobName(context.getJobDetail().getKey().getName())
                .jobGroup(context.getJobDetail().getKey().getGroup())
                .triggerName(context.getTrigger().getKey().getName())
                .triggerGroup(context.getTrigger().getKey().getGroup())
                .data(data)
                .manualTrigger(manualTrigger)
                .attempt(attempt)
                .maxAttempts(maxAttempts)
                .quartzContext(context)
                .build();

        try {
            SpringUtils.getBean(handlerName, JobHandler.class).execute(executeContext);
            deleteRetryTriggerIfPresent(context);
        } catch (Exception exception) {
            if (attempt < maxAttempts) {
                scheduleRetry(context, attempt, delayMs, backoffMultiplier);
            }
            throw new JobExecutionException(exception);
        }
    }

    private void deleteRetryTriggerIfPresent(JobExecutionContext context) throws SchedulerException {
        TriggerKey retryTriggerKey = JobDataMapper.retryTriggerKey(
                context.getJobDetail().getKey().getName(),
                context.getJobDetail().getKey().getGroup()
        );
        context.getScheduler().unscheduleJob(retryTriggerKey);
    }

    /**
     * 调度重试触发器，使用指数退避算法计算延迟时间。
     */
    private void scheduleRetry(JobExecutionContext context,
                               int attempt,
                               long delayMs,
                               double backoffMultiplier) throws JobExecutionException {
        long nextDelay = Math.round(delayMs * Math.pow(backoffMultiplier, Math.max(0, attempt - 1)));
        try {
            Trigger retryTrigger = JobDataMapper.toRetryTrigger(context, attempt + 1, nextDelay);
            TriggerKey retryKey = retryTrigger.getKey();
            if (context.getScheduler().checkExists(retryKey)) {
                context.getScheduler().rescheduleJob(retryKey, retryTrigger);
                return;
            }
            context.getScheduler().scheduleJob(retryTrigger);
        } catch (SchedulerException exception) {
            throw new JobExecutionException("Failed to schedule retry trigger", exception);
        }
    }
}
