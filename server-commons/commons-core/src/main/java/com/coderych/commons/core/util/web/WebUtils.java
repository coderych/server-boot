package com.coderych.commons.core.util.web;

import com.coderych.commons.core.exception.InternalException;
import com.coderych.commons.core.util.STR;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;

/**
 * Web 请求工具类，提供当前请求、请求头和客户端 IP 获取。
 * <p>IP 获取按优先级遍历多个代理头，取第一个非空非 unknown 的值。</p>
 *
 * @author YCH
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class WebUtils {

    private static final String UNKNOWN_IP = "unknown";

    private static final String[] IP_HEADER_NAMES = {
            "X-Real-IP",
            "X-Forwarded-For",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_X_FORWARDED_FOR",
            "HTTP_X_FORWARDED",
            "HTTP_X_CLUSTER_CLIENT_IP",
            "HTTP_CLIENT_IP",
            "HTTP_FORWARDED_FOR",
            "HTTP_FORWARDED",
            "HTTP_VIA",
            "REMOTE_ADDR"
    };

    public static HttpServletRequest getRequest() {
        return getRequestOptional()
                .orElseThrow(() -> new InternalException("No current HttpServletRequest bound to the thread"));
    }

    public static Optional<HttpServletRequest> getRequestOptional() {
        Object attributes = RequestContextHolder.getRequestAttributes();
        if (attributes instanceof ServletRequestAttributes servletRequestAttributes) {
            return Optional.of(servletRequestAttributes.getRequest());
        }
        return Optional.empty();
    }

    public static String getHeader(String name) {
        return getRequestOptional().map(request -> request.getHeader(name)).orElse(null);
    }

    public static String getUserAgent() {
        return getHeader("User-Agent");
    }

    public static String getRequestUri() {
        return getRequestOptional().map(HttpServletRequest::getRequestURI).orElse(null);
    }

    public static String getMethod() {
        return getRequestOptional().map(HttpServletRequest::getMethod).orElse(null);
    }

    public static String getIpAddress() {
        HttpServletRequest request = getRequest();
        for (String headerName : IP_HEADER_NAMES) {
            String value = request.getHeader(headerName);
            if (STR.isBlank(value) || UNKNOWN_IP.equalsIgnoreCase(value)) {
                continue;
            }
            int index = value.indexOf(',');
            return index > 0 ? value.substring(0, index).trim() : value.trim();
        }
        return request.getRemoteAddr();
    }

    public static String getRemoteAddr() {
        return getRequestOptional().map(HttpServletRequest::getRemoteAddr).orElse(null);
    }
}
