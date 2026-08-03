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
    /**
     * 链路追踪 ID。
     */
    private final String traceId;
    /**
     * 请求方法。
     */
    private final String requestMethod;
    /**
     * 请求 URI。
     */
    private final String requestUri;
    /**
     * 客户端 IP 地址。
     */
    private final String ipAddress;
    /**
     * User-Agent 请求头。
     */
    private final String userAgent;
    /**
     * 控制器类名。
     */
    private final String controllerClass;
    /**
     * 控制器方法名。
     */
    private final String controllerMethod;
    /**
     * 用户 ID。
     */
    private final String userId;
    /**
     * 用户名。
     */
    private final String username;
    /**
     * 请求是否成功。
     */
    private final boolean success;
    /**
     * 请求耗时，单位为毫秒。
     */
    private final long duration;
    /**
     * 请求体。
     */
    private final String requestBody;
    /**
     * 响应体。
     */
    private final String responseBody;
    /**
     * 异常类型名称。
     */
    private final String exceptionName;
    /**
     * 异常信息。
     */
    private final String exceptionMessage;
    /**
     * 日志记录时间戳。
     */
    private final long timestamp;
}
