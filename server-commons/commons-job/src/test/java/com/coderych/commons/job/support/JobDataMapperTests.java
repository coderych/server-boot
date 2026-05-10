package com.coderych.commons.job.support;

import com.coderych.commons.job.model.JobRetryPolicy;
import com.coderych.commons.job.model.JobScheduleRequest;
import org.junit.jupiter.api.Test;
import org.quartz.*;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class JobDataMapperTests {

    private static JobRetryPolicy defaultRetryPolicy() {
        return new JobRetryPolicy(3, Duration.ofSeconds(1), 2.0);
    }

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

    @Test
    void toJobDetailShouldReturnJobDetailWithCorrectIdentity() {
        JobScheduleRequest request = cronRequest();
        JobRetryPolicy retryPolicy = defaultRetryPolicy();

        JobDetail jobDetail = JobDataMapper.toJobDetail(request, retryPolicy);

        assertEquals("testJob", jobDetail.getKey().getName());
        assertEquals("testGroup", jobDetail.getKey().getGroup());
        assertTrue(jobDetail.isDurable());
    }

    @Test
    void toJobDetailShouldSetCorrectJobDataMap() {
        JobScheduleRequest request = cronRequest();
        JobRetryPolicy retryPolicy = defaultRetryPolicy();

        JobDetail jobDetail = JobDataMapper.toJobDetail(request, retryPolicy);
        JobDataMap dataMap = jobDetail.getJobDataMap();

        assertEquals("testHandler", dataMap.getString(JobDataMapper.HANDLER_NAME));
        assertFalse(dataMap.getBooleanValue(JobDataMapper.MANUAL_TRIGGER));
        assertEquals(1, dataMap.getIntValue(JobDataMapper.ATTEMPT));
        assertEquals(3, dataMap.getIntValue(JobDataMapper.RETRY_MAX_ATTEMPTS));
        assertEquals(1000L, dataMap.getLongValue(JobDataMapper.RETRY_DELAY_MS));
        assertEquals(2.0, dataMap.getDoubleValue(JobDataMapper.RETRY_BACKOFF_MULTIPLIER));
    }

    @Test
    void toJobDetailShouldUseDelegatingQuartzJobClass() {
        JobScheduleRequest request = cronRequest();
        JobDetail jobDetail = JobDataMapper.toJobDetail(request, defaultRetryPolicy());

        assertEquals(DelegatingQuartzJob.class, jobDetail.getJobClass());
    }

    @Test
    void toTriggerShouldCreateCronTriggerWhenCronExpressionPresent() {
        JobScheduleRequest request = cronRequest();

        Trigger trigger = JobDataMapper.toTrigger(request);

        assertInstanceOf(CronTrigger.class, trigger);
        assertEquals("testJob" + JobDataMapper.TRIGGER_SUFFIX, trigger.getKey().getName());
        assertEquals("testGroup", trigger.getKey().getGroup());
    }

    @Test
    void toTriggerShouldCreateSimpleTriggerWhenStartAtPresent() {
        Instant startAt = Instant.now().plusSeconds(3600);
        JobScheduleRequest request = JobScheduleRequest.builder()
                .handlerName("testHandler")
                .jobName("testJob")
                .jobGroup("testGroup")
                .startAt(startAt)
                .data(Map.of())
                .build();

        Trigger trigger = JobDataMapper.toTrigger(request);

        assertInstanceOf(SimpleTrigger.class, trigger);
        assertEquals(Date.from(startAt), trigger.getStartTime());
    }

    @Test
    void readDataShouldReturnMapWhenDataExists() {
        JobDataMap dataMap = new JobDataMap();
        Map<String, Object> expected = Map.of("key1", "value1", "key2", 42);
        dataMap.put(JobDataMapper.JOB_DATA, expected);

        Map<String, Object> result = JobDataMapper.readData(dataMap);

        assertEquals(expected, result);
    }

    @Test
    void readDataShouldReturnEmptyMapWhenDataIsNull() {
        JobDataMap dataMap = new JobDataMap();

        Map<String, Object> result = JobDataMapper.readData(dataMap);

        assertTrue(result.isEmpty());
    }

    @Test
    void readDataShouldReturnEmptyMapWhenDataIsNotMap() {
        JobDataMap dataMap = new JobDataMap();
        dataMap.put(JobDataMapper.JOB_DATA, "not a map");

        Map<String, Object> result = JobDataMapper.readData(dataMap);

        assertTrue(result.isEmpty());
    }

    @Test
    void jobKeyShouldReturnCorrectKey() {
        JobScheduleRequest request = cronRequest();

        JobKey jobKey = JobDataMapper.jobKey(request);

        assertEquals("testJob", jobKey.getName());
        assertEquals("testGroup", jobKey.getGroup());
    }

    @Test
    void jobKeyWithNameAndGroupShouldReturnCorrectKey() {
        JobKey jobKey = JobDataMapper.jobKey("myJob", "myGroup");

        assertEquals("myJob", jobKey.getName());
        assertEquals("myGroup", jobKey.getGroup());
    }

    @Test
    void triggerKeyShouldReturnKeyWithSuffix() {
        TriggerKey triggerKey = JobDataMapper.triggerKey("myJob", "myGroup");

        assertEquals("myJob" + JobDataMapper.TRIGGER_SUFFIX, triggerKey.getName());
        assertEquals("myGroup", triggerKey.getGroup());
    }

    @Test
    void retryTriggerKeyShouldReturnKeyWithRetrySuffix() {
        TriggerKey retryTriggerKey = JobDataMapper.retryTriggerKey("myJob", "myGroup");

        assertEquals("myJob" + JobDataMapper.RETRY_TRIGGER_SUFFIX, retryTriggerKey.getName());
        assertEquals("myGroup", retryTriggerKey.getGroup());
    }

    @Test
    void toJobDataMapShouldContainAllRetryPolicyValues() {
        JobScheduleRequest request = cronRequest();
        JobRetryPolicy retryPolicy = new JobRetryPolicy(5, Duration.ofMillis(2000), 1.5);

        JobDataMap dataMap = JobDataMapper.toJobDataMap(request, retryPolicy);

        assertEquals(5, dataMap.getIntValue(JobDataMapper.RETRY_MAX_ATTEMPTS));
        assertEquals(2000L, dataMap.getLongValue(JobDataMapper.RETRY_DELAY_MS));
        assertEquals(1.5, dataMap.getDoubleValue(JobDataMapper.RETRY_BACKOFF_MULTIPLIER));
    }

    @Test
    void constantsShouldHaveExpectedValues() {
        assertEquals("commons.job.handlerName", JobDataMapper.HANDLER_NAME);
        assertEquals("commons.job.data", JobDataMapper.JOB_DATA);
        assertEquals("commons.job.manualTrigger", JobDataMapper.MANUAL_TRIGGER);
        assertEquals("commons.job.attempt", JobDataMapper.ATTEMPT);
        assertEquals("commons.job.retry.maxAttempts", JobDataMapper.RETRY_MAX_ATTEMPTS);
        assertEquals("commons.job.retry.delayMs", JobDataMapper.RETRY_DELAY_MS);
        assertEquals("commons.job.retry.backoffMultiplier", JobDataMapper.RETRY_BACKOFF_MULTIPLIER);
        assertEquals("-trigger", JobDataMapper.TRIGGER_SUFFIX);
        assertEquals("-retry-trigger", JobDataMapper.RETRY_TRIGGER_SUFFIX);
    }
}
