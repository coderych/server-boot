package com.coderych.commons.job.support;

import com.coderych.commons.job.model.JobExecuteContext;

/**
 * 任务处理器函数式接口。
 * <p>
 * 实现此接口并通过 Spring Bean 名称注册后，可由 {@link DelegatingQuartzJob} 自动发现并调用。
 *
 * @author YCH
 */
@FunctionalInterface
public interface JobHandler {

    void execute(JobExecuteContext context);
}
