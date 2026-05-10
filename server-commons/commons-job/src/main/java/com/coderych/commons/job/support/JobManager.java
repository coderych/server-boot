package com.coderych.commons.job.support;

import com.coderych.commons.job.model.JobScheduleRequest;
import org.quartz.Trigger;

import java.util.Map;

/**
 * 任务管理器接口，定义任务的调度、触发、暂停、恢复和删除等操作。
 *
 * @author YCH
 */
public interface JobManager {

    void schedule(JobScheduleRequest request);

    void reschedule(JobScheduleRequest request);

    void trigger(String jobName, String jobGroup);

    void trigger(String jobName, String jobGroup, Map<String, Object> data);

    void pause(String jobName, String jobGroup);

    void resume(String jobName, String jobGroup);

    boolean delete(String jobName, String jobGroup);

    boolean exists(String jobName, String jobGroup);

    Trigger.TriggerState getTriggerState(String jobName, String jobGroup);
}
