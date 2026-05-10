package com.coderych.commons.core.exception;

/**
 * 定时任务执行异常。
 *
 * @author YCH
 */
public class JobException extends RuntimeException {

    public JobException(String message) {
        super(message);
    }

    public JobException(String message, Throwable cause) {
        super(message, cause);
    }
}
