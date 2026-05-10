package com.coderych.commons.web.handler;

import com.coderych.commons.core.enums.ResultCode;
import com.coderych.commons.core.exception.BizException;
import com.coderych.commons.core.model.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.stream.Collectors;

/**
 * 全局异常处理器，将各类异常统一转换为 {@link R} 响应格式。
 * <p>覆盖业务异常、参数校验异常、请求体解析异常和未知异常。</p>
 *
 * @author YCH
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BizException.class)
    public R<?> handleBizException(BizException exception) {
        log.warn("业务异常: code={}, message={}", exception.getCode(), exception.getMessage());
        return R.fail(exception.getCode(), exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public R<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        return handleValidationErrors("参数校验失败", exception.getBindingResult());
    }

    @ExceptionHandler(BindException.class)
    public R<?> handleBindException(BindException exception) {
        return handleValidationErrors("参数绑定失败", exception.getBindingResult());
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public R<?> handleHandlerMethodValidationException(HandlerMethodValidationException exception) {
        String message = exception.getParameterValidationResults().stream()
                .flatMap(result -> result.getResolvableErrors().stream())
                .map(MessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining("; "));
        log.warn("方法参数校验失败: {}", message);
        return R.fail(ResultCode.VALIDATION_ERROR.getCode(), message);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public R<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException exception) {
        log.warn("请求体格式错误: {}", exception.getMessage());
        return R.fail(ResultCode.BAD_REQUEST.getCode(), "请求体格式错误");
    }

    @ExceptionHandler(Exception.class)
    public R<?> handleException(Exception exception) {
        log.error("未知异常", exception);
        return R.fail(ResultCode.ERROR);
    }

    private R<?> handleValidationErrors(String logMessage, BindingResult bindingResult) {
        String message = bindingResult.getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        log.warn("{}: {}", logMessage, message);
        return R.fail(ResultCode.BAD_REQUEST.getCode(), message);
    }
}
