package com.coderych.commons.core.exception;

import com.coderych.commons.core.enums.ResultCode;

/**
 * 内部操作异常，对应 {@link ResultCode#INTERNAL_ERROR}。
 *
 * @author YCH
 */
public class InternalException extends BizException {

    public InternalException(String message) {
        super(ResultCode.INTERNAL_ERROR, message);
    }

    public InternalException(String message, Throwable cause) {
        super(ResultCode.INTERNAL_ERROR, message, cause);
    }
}
