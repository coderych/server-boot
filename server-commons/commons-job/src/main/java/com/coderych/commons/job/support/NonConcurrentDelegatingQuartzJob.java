package com.coderych.commons.job.support;

import org.quartz.DisallowConcurrentExecution;

/**
 * 禁止同一任务并发执行的 Quartz 委托任务。
 */
@DisallowConcurrentExecution
public class NonConcurrentDelegatingQuartzJob extends DelegatingQuartzJob {
}
