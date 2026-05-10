package com.coderych.commons.log.model;

import lombok.Builder;
import lombok.Getter;

/**
 * 操作日志记录模型，封装一次请求的完整日志信息。
 * <p>包含请求上下文（方法、URI、IP）、调用者信息、执行结果、耗时及异常信息等，
 * 同时作为 Spring ApplicationEvent 发布，供其他模块监听处理。</p>
 *
 * @author YCH
 */
@Getter
@Builder
public class LogRecord {
    private final String traceId;
    private final String requestMethod;
    private final String requestUri;
    private final String ipAddress;
    private final String userAgent;
    private final String controllerClass;
    private final String controllerMethod;
    private final String userId;
    private final String username;
    private final boolean success;
    private final long duration;
    private final String requestBody;
    private final String responseBody;
    private final String exceptionName;
    private final String exceptionMessage;
    private final long timestamp;
}
