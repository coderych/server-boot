package com.coderych.commons.log.support;

import com.coderych.commons.core.util.JSON;
import com.coderych.commons.log.autoconfigure.LogProperties;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 方法参数序列化器，将 Controller 方法的入参和返回值转换为可记录的日志字符串。
 * <p>自动跳过不适合序列化的类型（如文件流、HTTP 对象等），
 * 并对序列化结果进行敏感字段脱敏和长度截断处理。</p>
 *
 * @author YCH
 */
@RequiredArgsConstructor
public class ParameterSerializer {
    private final LogProperties properties;
    private final SensitiveValueMasker sensitiveValueMasker;

    public String serializeArguments(Object[] arguments) {
        List<Object> values = new ArrayList<>();
        if (arguments != null) {
            for (Object argument : arguments) {
                if (!shouldSkip(argument)) {
                    values.add(toLogValue(argument));
                }
            }
        }
        return truncate(JSON.toJson(sensitiveValueMasker.mask(values)));
    }

    public String serializeResult(Object result) {
        if (shouldSkip(result)) {
            return null;
        }
        return truncate(JSON.toJson(sensitiveValueMasker.mask(toLogValue(result))));
    }

    private Object toLogValue(Object value) {
        try {
            return JSON.toJson(value);
        } catch (RuntimeException exception) {
            return "<<non-serializable: " + value.getClass().getName() + ">>";
        }
    }

    private String truncate(String value) {
        int maxLength = properties.getMaxLength();
        if (value == null || maxLength <= 0 || value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, maxLength) + "...";
    }

    /**
     * 判断参数是否应跳过序列化。
     * <p>跳过不可序列化或不适合记录的类型：文件流、HTTP 请求/响应对象、
     * 表单绑定结果、字节流等。纯 MultipartFile 集合也会被整体跳过。</p>
     */
    private boolean shouldSkip(Object value) {
        if (value == null) {
            return false;
        }
        if (value instanceof MultipartFile
                || value instanceof HttpServletRequest
                || value instanceof HttpServletResponse
                || value instanceof BindingResult
                || value instanceof InputStream
                || value instanceof OutputStream
                || value instanceof byte[]) {
            return true;
        }
        if (value instanceof MultipartFile[]) {
            return true;
        }
        if (value instanceof Collection<?> collection) {
            return collection.stream().allMatch(MultipartFile.class::isInstance);
        }
        return false;
    }
}
