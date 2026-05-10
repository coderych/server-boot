package com.coderych.commons.core.model;

import com.coderych.commons.core.enums.ResultCode;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 统一响应封装。
 *
 * @param <T> 响应数据类型
 * @author YCH
 */
@Data
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class R<T> implements Serializable {
    public static final int SUCCESS_CODE = ResultCode.SUCCESS.getCode();
    public static final String SUCCESS_MESSAGE = ResultCode.SUCCESS.getMessage();
    public static final int ERROR_CODE = ResultCode.ERROR.getCode();
    public static final String ERROR_MESSAGE = ResultCode.ERROR.getMessage();
    @Serial
    private static final long serialVersionUID = 1L;

    private boolean success;

    private int code;

    private String message;

    private T data;

    private long timestamp;

    private R(boolean success, int code, String message, T data) {
        this.success = success;
        this.code = code;
        this.message = message;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }

    public static R<?> ok() {
        return new R<>(true, SUCCESS_CODE, SUCCESS_MESSAGE, null);
    }

    public static <T> R<T> ok(T data) {
        return new R<>(true, SUCCESS_CODE, SUCCESS_MESSAGE, data);
    }

    public static <T> R<T> ok(String message, T data) {
        return new R<>(true, SUCCESS_CODE, message, data);
    }

    public static R<?> fail() {
        return new R<>(false, ERROR_CODE, ERROR_MESSAGE, null);
    }

    public static R<?> fail(String message) {
        return new R<>(false, ERROR_CODE, message, null);
    }

    public static R<?> fail(int code, String message) {
        return new R<>(false, code, message, null);
    }

    public static R<?> fail(ResultCode resultCode) {
        return new R<>(false, resultCode.getCode(), resultCode.getMessage(), null);
    }

    public static <T> R<T> fail(ResultCode resultCode, T data) {
        return new R<>(false, resultCode.getCode(), resultCode.getMessage(), data);
    }
}
