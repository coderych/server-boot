package com.coderych.commons.log.aspect;

import com.coderych.commons.core.util.spring.SpringUtils;
import com.coderych.commons.core.util.web.WebUtils;
import com.coderych.commons.log.annotation.LogIgnore;
import com.coderych.commons.log.autoconfigure.LogProperties;
import com.coderych.commons.log.model.LogRecord;
import com.coderych.commons.log.support.ParameterSerializer;
import com.coderych.commons.satoken.core.LoginUser;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.MDC;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.annotation.AnnotatedElementUtils;

import java.lang.reflect.Method;

/**
 * 自动操作日志切面，拦截所有 Controller 层的请求并记录操作日志。
 * <p>自动采集请求方法、URI、IP、耗时、请求参数、响应结果等信息，
 * 同时支持通过 {@link LogIgnore} 注解排除不需要记录的接口。</p>
 * <p>记录完成后通过 Spring 事件机制发布 {@link LogRecord}，便于后续扩展处理。</p>
 *
 * @author YCH
 */
@Slf4j
@Aspect
@RequiredArgsConstructor
public class AutoLogAspect {
    private final LogProperties properties;
    private final ParameterSerializer parameterSerializer;

    @Around("@within(org.springframework.web.bind.annotation.RestController) "
            + "|| (@within(org.springframework.stereotype.Controller) && @annotation(org.springframework.web.bind.annotation.ResponseBody))")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Class<?> targetClass = AopUtils.getTargetClass(joinPoint.getTarget());
        Method method = AopUtils.getMostSpecificMethod(signature.getMethod(), targetClass);
        if (shouldIgnore(method, targetClass)) {
            return joinPoint.proceed();
        }

        log.info(">>>>>>>>> AutoLogAspect —— 自动操作日志拦截: {}.{}", targetClass.getSimpleName(), method.getName());
        HttpServletRequest request = currentRequest();
        long startTime = System.currentTimeMillis();
        String requestBody = properties.isIncludeArgs() ? parameterSerializer.serializeArguments(joinPoint.getArgs()) : null;
        try {
            Object result = joinPoint.proceed();
            LogRecord record = buildRecord(method, targetClass, request, startTime, requestBody,
                    properties.isIncludeResult() ? parameterSerializer.serializeResult(result) : null, null);
            logRecord(record, null);
            publishEvent(record);
            return result;
        } catch (Throwable throwable) {
            LogRecord record = buildRecord(method, targetClass, request, startTime, requestBody, null, throwable);
            logRecord(record, throwable);
            publishEvent(record);
            throw throwable;
        }
    }

    private boolean shouldIgnore(Method method, Class<?> targetClass) {
        return AnnotatedElementUtils.hasAnnotation(method, LogIgnore.class)
                || AnnotatedElementUtils.hasAnnotation(targetClass, LogIgnore.class);
    }

    private LogRecord buildRecord(Method method,
                                  Class<?> targetClass,
                                  HttpServletRequest request,
                                  long startTime,
                                  String requestBody,
                                  String responseBody,
                                  Throwable throwable) {
        return LogRecord.builder()
                .traceId(MDC.get("traceId"))
                .requestMethod(request == null ? null : request.getMethod())
                .requestUri(request == null ? null : request.getRequestURI())
                .ipAddress(WebUtils.getIpAddress())
                .userAgent(WebUtils.getUserAgent())
                .controllerClass(targetClass.getName())
                .controllerMethod(method.getName())
                .userId(LoginUser.getLoginUserIdOrDefault("unknown"))
                .username(LoginUser.getLoginUsernameOrDefault("unknown"))
                .success(throwable == null)
                .duration(System.currentTimeMillis() - startTime)
                .requestBody(requestBody)
                .responseBody(responseBody)
                .exceptionName(throwable == null ? null : throwable.getClass().getName())
                .exceptionMessage(throwable == null ? null : throwable.getMessage())
                .timestamp(System.currentTimeMillis())
                .build();
    }

    private void publishEvent(LogRecord record) {
        try {
            SpringUtils.publishEvent(record);
        } catch (RuntimeException exception) {
            log.error("Failed to publish log record event", exception);
        }
    }

    private void logRecord(LogRecord record, Throwable throwable) {
        if (throwable == null) {
            log.info("requestMethod={} requestUri={} ipAddress={} userAgent={} controller={}.{} userId={} username={} success={} duration={}ms request={} response={}",
                    record.getRequestMethod(), record.getRequestUri(), record.getIpAddress(), record.getUserAgent(),
                    record.getControllerClass(), record.getControllerMethod(), record.getUserId(), record.getUsername(),
                    record.isSuccess(), record.getDuration(), record.getRequestBody(), record.getResponseBody());
        } else {
            log.error("requestMethod={} requestUri={} ipAddress={} userAgent={} controller={}.{} userId={} username={} success={} duration={}ms request={} exceptionName={} exceptionMessage={}",
                    record.getRequestMethod(), record.getRequestUri(), record.getIpAddress(), record.getUserAgent(),
                    record.getControllerClass(), record.getControllerMethod(), record.getUserId(), record.getUsername(),
                    record.isSuccess(), record.getDuration(), record.getRequestBody(),
                    record.getExceptionName(), record.getExceptionMessage(), throwable);
        }
    }

    private HttpServletRequest currentRequest() {
        return WebUtils.getRequestOptional().orElse(null);
    }
}
