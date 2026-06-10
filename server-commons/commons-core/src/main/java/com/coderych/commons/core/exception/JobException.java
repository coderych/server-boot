package com.coderych.commons.core.exception;

import com.coderych.commons.core.enums.ResultCode;

/**
 * 定时任务执行异常。
 *
 * @author YCH
 */
public class JobException extends BizException {

    public JobException(String message) {
        super(ResultCode.OPERATION_FAILED, message);
    }

    public JobException(String message, Throwable cause) {
        super(ResultCode.OPERATION_FAILED, message, cause);
    }
}
