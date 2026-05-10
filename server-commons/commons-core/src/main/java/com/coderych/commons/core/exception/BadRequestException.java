package com.coderych.commons.core.exception;

import com.coderych.commons.core.enums.ResultCode;

/**
 * 请求参数错误异常，对应 {@link ResultCode#BAD_REQUEST}。
 *
 * @author YCH
 */
public class BadRequestException extends BizException {

    public BadRequestException(String message) {
        super(ResultCode.BAD_REQUEST, message);
    }

    public BadRequestException(String message, Throwable cause) {
        super(ResultCode.BAD_REQUEST, message, cause);
    }
}
