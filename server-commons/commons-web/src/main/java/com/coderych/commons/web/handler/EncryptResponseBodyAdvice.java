package com.coderych.commons.web.handler;

import com.coderych.commons.core.util.JSON;
import com.coderych.commons.core.util.spring.AnnotationUtils;
import com.coderych.commons.web.annotation.Crypto;
import com.coderych.commons.web.crypto.CryptoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * 全局响应体 Advice，加密范围由业务实现的 {@link CryptoService} 决定：
 * {@code @Crypto} 注解接口按注解加密；无注解接口仅在全局响应加密（{@link CryptoService#globalEncrypt()}）下
 * 使用默认算法加密，注解 {@code encrypt=false} 的接口会从全局加密中排除。
 * 未提供 {@link CryptoService} 实现或算法被动态关闭时按明文透传。
 * 响应 Content-Type 使用 {@link CryptoService#defaultContentType()}。
 *
 * @author YCH
 */
@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class EncryptResponseBodyAdvice implements ResponseBodyAdvice<Object> {
    private final ObjectProvider<CryptoService> cryptoServiceProvider;

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        CryptoService cryptoService = cryptoServiceProvider.getIfAvailable();
        if (cryptoService == null) {
            return false;
        }
        Crypto crypto = AnnotationUtils.resolve(returnType, Crypto.class);
        if (crypto != null) {
            return crypto.encrypt() && cryptoService.enabled(crypto.algorithm());
        }
        return cryptoService.globalEncrypt() && cryptoService.enabled(cryptoService.defaultAlgorithm());
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {
        CryptoService cryptoService = cryptoServiceProvider.getIfAvailable();
        Crypto crypto = AnnotationUtils.resolve(returnType, Crypto.class);
        String algorithmName = crypto != null ? crypto.algorithm() : cryptoService.defaultAlgorithm();
        response.getHeaders().setContentType(MediaType.parseMediaType(cryptoService.defaultContentType()));

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
        return cryptoService.encrypt(json, algorithmName);
    }
}
