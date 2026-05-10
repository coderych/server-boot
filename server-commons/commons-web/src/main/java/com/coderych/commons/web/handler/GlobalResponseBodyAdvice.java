package com.coderych.commons.web.handler;

import com.coderych.commons.core.util.JSON;
import com.coderych.commons.core.util.spring.AnnotationUtils;
import com.coderych.commons.web.annotation.Crypto;
import com.coderych.commons.web.autoconfigure.WebProperties;
import com.coderych.commons.web.util.Cryptos;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * 全局响应体 Advice，对标注了 {@link Crypto} 注解且 {@code encrypt=true} 的接口自动加密响应体。
 * <p>响应 Content-Type 会被替换为配置的默认值（默认 {@code text/plain}）。</p>
 *
 * @author YCH
 */
@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalResponseBodyAdvice implements ResponseBodyAdvice<Object> {
    private final WebProperties webProperties;

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        Crypto crypto = AnnotationUtils.resolve(returnType, Crypto.class);
        return crypto != null && crypto.encrypt();
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {
        Crypto crypto = AnnotationUtils.resolve(returnType, Crypto.class);
        String algorithmName = crypto.algorithm();
        String contentType = webProperties.getCrypto().getDefaultContentType();
        response.getHeaders().setContentType(MediaType.parseMediaType(contentType));

        String json;
        if (body instanceof String stringBody) {
            json = stringBody;
        } else {
            try {
                json = JSON.toJson(body);
            } catch (Exception exception) {
                throw new IllegalStateException("响应序列化失败", exception);
            }
        }
        log.debug("响应加密: algorithm={}", algorithmName);
        return Cryptos.encrypt(json, algorithmName);
    }
}
