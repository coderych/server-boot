package com.coderych.commons.job.support;

import cn.hutool.extra.spring.SpringUtil;
import com.coderych.commons.job.model.JobExecuteContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quartz.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DelegatingQuartzJobTests {

    @Mock
    private Scheduler scheduler;

    @Mock
    private JobDetail jobDetail;

    @Mock
    private Trigger trigger;

    @Mock
    private JobExecutionContext jobExecutionContext;

    @Mock
    private JobHandler jobHandler;

    private DelegatingQuartzJob delegatingJob;

    @BeforeEach
    void setUp() throws SchedulerException {
        delegatingJob = new DelegatingQuartzJob();

        JobKey jobKey = new JobKey("testJob", "testGroup");
        TriggerKey triggerKey = new TriggerKey("testJob-trigger", "testGroup");

        lenient().when(jobExecutionContext.getScheduler()).thenReturn(scheduler);
        when(jobExecutionContext.getJobDetail()).thenReturn(jobDetail);
        when(jobExecutionContext.getTrigger()).thenReturn(trigger);
        when(jobDetail.getKey()).thenReturn(jobKey);
        when(trigger.getKey()).thenReturn(triggerKey);
    }

    private JobDataMap createJobDataMap(int attempt, int maxAttempts, long delayMs, double backoffMultiplier) {
        JobDataMap dataMap = new JobDataMap();
        dataMap.put("commons.job.handlerName", "testHandler");
        dataMap.put("commons.job.data", new java.util.HashMap<>());
        dataMap.put("commons.job.manualTrigger", false);
        dataMap.put("commons.job.attempt", attempt);
        dataMap.put("commons.job.retry.maxAttempts", maxAttempts);
        dataMap.put("commons.job.retry.delayMs", delayMs);
        dataMap.put("commons.job.retry.backoffMultiplier", backoffMultiplier);
        return dataMap;
    }

    @Test
    void shouldExecuteHandlerSuccessfully() throws Exception {
        JobDataMap dataMap = createJobDataMap(1, 3, 1000, 2.0);
        when(jobExecutionContext.getMergedJobDataMap()).thenReturn(dataMap);

        try (MockedStatic<SpringUtil> springUtilMock = mockStatic(SpringUtil.class)) {
            springUtilMock.when(() -> SpringUtil.getBean(eq("testHandler"), eq(JobHandler.class)))
                    .thenReturn(jobHandler);

            delegatingJob.execute(jobExecutionContext);

            verify(jobHandler).execute(any(JobExecuteContext.class));
            verify(scheduler).unscheduleJob(any(TriggerKey.class));
        }
    }

    @Test
    void shouldScheduleRetryWhenHandlerFailsAndAttemptsRemaining() throws Exception {
        JobDataMap dataMap = createJobDataMap(1, 3, 1000, 2.0);
        when(jobExecutionContext.getMergedJobDataMap()).thenReturn(dataMap);
        doThrow(new RuntimeException("handler failed")).when(jobHandler).execute(any());

        try (MockedStatic<SpringUtil> springUtilMock = mockStatic(SpringUtil.class);
             MockedStatic<JobDataMapper> mapperMock = mockStatic(JobDataMapper.class)) {

            springUtilMock.when(() -> SpringUtil.getBean(eq("testHandler"), eq(JobHandler.class)))
                    .thenReturn(jobHandler);

            TriggerKey retryKey = new TriggerKey("testJob-retry-trigger", "testGroup");
            Trigger retryTrigger = mock(Trigger.class);
            when(retryTrigger.getKey()).thenReturn(retryKey);
            mapperMock.when(() -> JobDataMapper.toRetryTrigger(any(), eq(2), eq(1000L)))
                    .thenReturn(retryTrigger);
            mapperMock.when(() -> JobDataMapper.retryTriggerKey("testJob", "testGroup"))
                    .thenReturn(retryKey);
            when(scheduler.checkExists(retryKey)).thenReturn(false);
            when(scheduler.scheduleJob(retryTrigger)).thenReturn(null);

            assertThrows(JobExecutionException.class,
                    () -> delegatingJob.execute(jobExecutionContext));

            verify(scheduler).scheduleJob(retryTrigger);
        }
    }

    @Test
    void shouldNotScheduleRetryWhenMaxAttemptsReached() throws Exception {
        JobDataMap dataMap = createJobDataMap(3, 3, 1000, 2.0);
        when(jobExecutionContext.getMergedJobDataMap()).thenReturn(dataMap);
        doThrow(new RuntimeException("handler failed")).when(jobHandler).execute(any());

        try (MockedStatic<SpringUtil> springUtilMock = mockStatic(SpringUtil.class)) {
            springUtilMock.when(() -> SpringUtil.getBean(eq("testHandler"), eq(JobHandler.class)))
                    .thenReturn(jobHandler);

            assertThrows(JobExecutionException.class,
                    () -> delegatingJob.execute(jobExecutionContext));

            verify(scheduler, never()).scheduleJob(any(Trigger.class));
        }
    }

    @Test
    void shouldRescheduleRetryWhenRetryTriggerAlreadyExists() throws Exception {
        JobDataMap dataMap = createJobDataMap(1, 3, 1000, 2.0);
        when(jobExecutionContext.getMergedJobDataMap()).thenReturn(dataMap);
        doThrow(new RuntimeException("handler failed")).when(jobHandler).execute(any());

        try (MockedStatic<SpringUtil> springUtilMock = mockStatic(SpringUtil.class);
             MockedStatic<JobDataMapper> mapperMock = mockStatic(JobDataMapper.class)) {

            springUtilMock.when(() -> SpringUtil.getBean(eq("testHandler"), eq(JobHandler.class)))
                    .thenReturn(jobHandler);

            TriggerKey retryKey = new TriggerKey("testJob-retry-trigger", "testGroup");
            Trigger retryTrigger = mock(Trigger.class);
            when(retryTrigger.getKey()).thenReturn(retryKey);
            mapperMock.when(() -> JobDataMapper.toRetryTrigger(any(), eq(2), eq(1000L)))
                    .thenReturn(retryTrigger);
            mapperMock.when(() -> JobDataMapper.retryTriggerKey("testJob", "testGroup"))
                    .thenReturn(retryKey);
            when(scheduler.checkExists(retryKey)).thenReturn(true);
            when(scheduler.rescheduleJob(retryKey, retryTrigger)).thenReturn(null);

            assertThrows(JobExecutionException.class,
                    () -> delegatingJob.execute(jobExecutionContext));

            verify(scheduler).rescheduleJob(retryKey, retryTrigger);
            verify(scheduler, never()).scheduleJob(any(Trigger.class));
        }
    }

    @Test
    void shouldBuildCorrectExecuteContext() throws Exception {
        JobDataMap dataMap = createJobDataMap(2, 5, 500, 1.5);
        dataMap.put("commons.job.manualTrigger", true);
        java.util.Map<String, Object> jobData = new java.util.HashMap<>();
        jobData.put("key1", "value1");
        dataMap.put("commons.job.data", jobData);
        when(jobExecutionContext.getMergedJobDataMap()).thenReturn(dataMap);

        try (MockedStatic<SpringUtil> springUtilMock = mockStatic(SpringUtil.class)) {
            springUtilMock.when(() -> SpringUtil.getBean(eq("testHandler"), eq(JobHandler.class)))
                    .thenReturn(jobHandler);

            delegatingJob.execute(jobExecutionContext);

            ArgumentCaptor<JobExecuteContext> captor = ArgumentCaptor.forClass(JobExecuteContext.class);
            verify(jobHandler).execute(captor.capture());

            JobExecuteContext context = captor.getValue();
            assertEquals("testHandler", context.getHandlerName());
            assertEquals("testJob", context.getJobName());
            assertEquals("testGroup", context.getJobGroup());
            assertEquals("testJob-trigger", context.getTriggerName());
            assertEquals("testGroup", context.getTriggerGroup());
            assertTrue(context.isManualTrigger());
            assertEquals(2, context.getAttempt());
            assertEquals(5, context.getMaxAttempts());
            assertEquals("value1", context.getData().get("key1"));
            assertSame(jobExecutionContext, context.getQuartzContext());
        }
    }

    @Test
    void shouldCalculateBackoffCorrectlyForRetry() throws Exception {
        JobDataMap dataMap = createJobDataMap(2, 5, 1000, 2.0);
        when(jobExecutionContext.getMergedJobDataMap()).thenReturn(dataMap);
        doThrow(new RuntimeException("handler failed")).when(jobHandler).execute(any());

        try (MockedStatic<SpringUtil> springUtilMock = mockStatic(SpringUtil.class);
             MockedStatic<JobDataMapper> mapperMock = mockStatic(JobDataMapper.class)) {

            springUtilMock.when(() -> SpringUtil.getBean(eq("testHandler"), eq(JobHandler.class)))
                    .thenReturn(jobHandler);

            TriggerKey retryKey = new TriggerKey("testJob-retry-trigger", "testGroup");
            Trigger retryTrigger = mock(Trigger.class);
            when(retryTrigger.getKey()).thenReturn(retryKey);

            long expectedDelay = Math.round(1000 * Math.pow(2.0, Math.max(0, 2 - 1)));

            mapperMock.when(() -> JobDataMapper.toRetryTrigger(any(), eq(3), eq(expectedDelay)))
                    .thenReturn(retryTrigger);
            mapperMock.when(() -> JobDataMapper.retryTriggerKey("testJob", "testGroup"))
                    .thenReturn(retryKey);
            when(scheduler.checkExists(retryKey)).thenReturn(false);
            when(scheduler.scheduleJob(retryTrigger)).thenReturn(null);

            assertThrows(JobExecutionException.class,
                    () -> delegatingJob.execute(jobExecutionContext));

            verify(scheduler).scheduleJob(retryTrigger);
        }
    }
}
