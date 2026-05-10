package com.coderych.commons.job.support;

import com.coderych.commons.job.model.JobRetryPolicy;
import com.coderych.commons.job.model.JobScheduleRequest;
import com.coderych.commons.job.util.QuartzJobManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quartz.*;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuartzJobManagerTests {

    private final JobRetryPolicy defaultRetryPolicy = new JobRetryPolicy(3, Duration.ofSeconds(1), 2.0);
    @Mock
    private Scheduler scheduler;

    private static JobScheduleRequest cronRequest() {
        return JobScheduleRequest.builder()
                .handlerName("testHandler")
                .jobName("testJob")
                .jobGroup("testGroup")
                .cronExpression("0 0/5 * * * ?")
                .data(Map.of("key", "value"))
                .build();
    }

    private static JobScheduleRequest simpleTriggerRequest() {
        return JobScheduleRequest.builder()
                .handlerName("testHandler")
                .jobName("testJob")
                .jobGroup("testGroup")
                .startAt(Instant.now().plusSeconds(3600))
                .data(Map.of("key", "value"))
                .build();
    }

    @BeforeEach
    void setUp() {
        QuartzJobManager.init(scheduler, defaultRetryPolicy);
    }

    @Test
    void scheduleShouldThrowWhenRequestIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> QuartzJobManager.schedule(null));
    }

    @Test
    void scheduleShouldThrowWhenHandlerNameIsBlank() {
        JobScheduleRequest request = JobScheduleRequest.builder()
                .handlerName("")
                .jobName("testJob")
                .cronExpression("0 0/5 * * * ?")
                .build();

        assertThrows(IllegalArgumentException.class,
                () -> QuartzJobManager.schedule(request));
    }

    @Test
    void scheduleShouldThrowWhenJobNameIsBlank() {
        JobScheduleRequest request = JobScheduleRequest.builder()
                .handlerName("testHandler")
                .jobName("")
                .cronExpression("0 0/5 * * * ?")
                .build();

        assertThrows(IllegalArgumentException.class,
                () -> QuartzJobManager.schedule(request));
    }

    @Test
    void scheduleShouldThrowWhenBothCronAndStartAtProvided() {
        JobScheduleRequest request = JobScheduleRequest.builder()
                .handlerName("testHandler")
                .jobName("testJob")
                .cronExpression("0 0/5 * * * ?")
                .startAt(Instant.now())
                .build();

        assertThrows(IllegalArgumentException.class,
                () -> QuartzJobManager.schedule(request));
    }

    @Test
    void scheduleShouldThrowWhenNeitherCronNorStartAtProvided() {
        JobScheduleRequest request = JobScheduleRequest.builder()
                .handlerName("testHandler")
                .jobName("testJob")
                .build();

        assertThrows(IllegalArgumentException.class,
                () -> QuartzJobManager.schedule(request));
    }

    @Test
    void scheduleShouldScheduleJobSuccessfully() throws SchedulerException {
        JobScheduleRequest request = cronRequest();
        when(scheduler.checkExists(any(JobKey.class))).thenReturn(false);
        when(scheduler.scheduleJob(any(JobDetail.class), any(Trigger.class))).thenReturn(null);

        QuartzJobManager.schedule(request);

        verify(scheduler).checkExists(any(JobKey.class));
        verify(scheduler).scheduleJob(any(JobDetail.class), any(Trigger.class));
    }

    @Test
    void scheduleShouldThrowWhenJobAlreadyExists() throws SchedulerException {
        JobScheduleRequest request = cronRequest();
        when(scheduler.checkExists(any(JobKey.class))).thenReturn(true);

        assertThrows(IllegalStateException.class,
                () -> QuartzJobManager.schedule(request));
    }

    @Test
    void scheduleShouldTriggerImmediatelyWhenImmediateIsTrue() throws SchedulerException {
        JobScheduleRequest request = JobScheduleRequest.builder()
                .handlerName("testHandler")
                .jobName("testJob")
                .jobGroup("testGroup")
                .cronExpression("0 0/5 * * * ?")
                .data(Map.of("key", "value"))
                .immediate(true)
                .build();
        when(scheduler.checkExists(any(JobKey.class))).thenReturn(false);
        when(scheduler.scheduleJob(any(JobDetail.class), any(Trigger.class))).thenReturn(null);

        QuartzJobManager.schedule(request);

        verify(scheduler).triggerJob(any(JobKey.class), any(JobDataMap.class));
    }

    @Test
    void scheduleShouldUseRequestRetryPolicyWhenProvided() throws SchedulerException {
        JobRetryPolicy customPolicy = new JobRetryPolicy(5, Duration.ofMillis(500), 1.5);
        JobScheduleRequest request = JobScheduleRequest.builder()
                .handlerName("testHandler")
                .jobName("testJob")
                .jobGroup("testGroup")
                .cronExpression("0 0/5 * * * ?")
                .data(Map.of())
                .retryPolicy(customPolicy)
                .build();
        when(scheduler.checkExists(any(JobKey.class))).thenReturn(false);
        when(scheduler.scheduleJob(any(JobDetail.class), any(Trigger.class))).thenReturn(null);

        QuartzJobManager.schedule(request);

        verify(scheduler).scheduleJob(any(JobDetail.class), any(Trigger.class));
    }

    @Test
    void rescheduleShouldScheduleNewJobWhenNotExists() throws SchedulerException {
        JobScheduleRequest request = cronRequest();
        when(scheduler.checkExists(any(JobKey.class))).thenReturn(false);
        when(scheduler.scheduleJob(any(JobDetail.class), any(Trigger.class))).thenReturn(null);

        QuartzJobManager.reschedule(request);

        verify(scheduler).scheduleJob(any(JobDetail.class), any(Trigger.class));
    }

    @Test
    void rescheduleShouldRescheduleExistingJob() throws SchedulerException {
        JobScheduleRequest request = cronRequest();
        when(scheduler.checkExists(any(JobKey.class))).thenReturn(true);
        doNothing().when(scheduler).addJob(any(JobDetail.class), eq(true), eq(true));
        when(scheduler.rescheduleJob(any(TriggerKey.class), any(Trigger.class))).thenReturn(null);

        QuartzJobManager.reschedule(request);

        verify(scheduler).addJob(any(JobDetail.class), eq(true), eq(true));
        verify(scheduler).rescheduleJob(any(TriggerKey.class), any(Trigger.class));
    }

    @Test
    void triggerShouldTriggerJob() throws SchedulerException {
        QuartzJobManager.trigger("testJob", "testGroup");

        verify(scheduler).triggerJob(any(JobKey.class), any(JobDataMap.class));
    }

    @Test
    void triggerWithDataShouldTriggerJobWithData() throws SchedulerException {
        Map<String, Object> data = Map.of("key", "value");

        QuartzJobManager.trigger("testJob", "testGroup", data);

        verify(scheduler).triggerJob(any(JobKey.class), any(JobDataMap.class));
    }

    @Test
    void triggerShouldHandleNullData() throws SchedulerException {
        QuartzJobManager.trigger("testJob", "testGroup", null);

        verify(scheduler).triggerJob(any(JobKey.class), any(JobDataMap.class));
    }

    @Test
    void pauseShouldPauseJob() throws SchedulerException {
        QuartzJobManager.pause("testJob", "testGroup");

        verify(scheduler).pauseJob(any(JobKey.class));
    }

    @Test
    void resumeShouldResumeJob() throws SchedulerException {
        QuartzJobManager.resume("testJob", "testGroup");

        verify(scheduler).resumeJob(any(JobKey.class));
    }

    @Test
    void deleteShouldReturnTrueWhenJobDeleted() throws SchedulerException {
        when(scheduler.deleteJob(any(JobKey.class))).thenReturn(true);

        boolean result = QuartzJobManager.delete("testJob", "testGroup");

        assertTrue(result);
    }

    @Test
    void deleteShouldReturnFalseWhenJobNotFound() throws SchedulerException {
        when(scheduler.deleteJob(any(JobKey.class))).thenReturn(false);

        boolean result = QuartzJobManager.delete("testJob", "testGroup");

        assertFalse(result);
    }

    @Test
    void existsShouldReturnTrueWhenJobExists() throws SchedulerException {
        when(scheduler.checkExists(any(JobKey.class))).thenReturn(true);

        boolean result = QuartzJobManager.exists("testJob", "testGroup");

        assertTrue(result);
    }

    @Test
    void existsShouldReturnFalseWhenJobNotExists() throws SchedulerException {
        when(scheduler.checkExists(any(JobKey.class))).thenReturn(false);

        boolean result = QuartzJobManager.exists("testJob", "testGroup");

        assertFalse(result);
    }

    @Test
    void getTriggerStateShouldReturnState() throws SchedulerException {
        when(scheduler.getTriggerState(any(TriggerKey.class))).thenReturn(Trigger.TriggerState.NORMAL);

        Trigger.TriggerState state = QuartzJobManager.getTriggerState("testJob", "testGroup");

        assertEquals(Trigger.TriggerState.NORMAL, state);
    }

    @Test
    void scheduleShouldWrapSchedulerExceptionAsIllegalState() throws SchedulerException {
        JobScheduleRequest request = cronRequest();
        when(scheduler.checkExists(any(JobKey.class))).thenThrow(new SchedulerException("test error"));

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> QuartzJobManager.schedule(request));

        assertEquals("Failed to schedule job", exception.getMessage());
        assertInstanceOf(SchedulerException.class, exception.getCause());
    }

    @Test
    void initShouldThrowWhenSchedulerIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> QuartzJobManager.init(null, defaultRetryPolicy));
    }

    @Test
    void initShouldThrowWhenRetryPolicyIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> QuartzJobManager.init(scheduler, null));
    }

    @Test
    void scheduleShouldWorkWithSimpleTrigger() throws SchedulerException {
        JobScheduleRequest request = simpleTriggerRequest();
        when(scheduler.checkExists(any(JobKey.class))).thenReturn(false);
        when(scheduler.scheduleJob(any(JobDetail.class), any(Trigger.class))).thenReturn(null);

        QuartzJobManager.schedule(request);

        verify(scheduler).scheduleJob(any(JobDetail.class), any(Trigger.class));
    }
}
