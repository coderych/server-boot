package com.coderych.commons.web.handler;

import com.coderych.commons.core.util.spring.AnnotationUtils;
import com.coderych.commons.web.annotation.Crypto;
import com.coderych.commons.web.autoconfigure.WebProperties;
import com.coderych.commons.web.crypto.CryptoService;
import com.coderych.commons.web.util.XssCleaner;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

/**
 * 全局请求体 Advice，解密范围由业务实现的 {@link CryptoService} 决定：
 * {@code @Crypto} 注解接口按注解解密；无注解接口仅在全局请求解密（{@link CryptoService#globalDecrypt()}）下
 * 使用默认算法解密，注解 {@code decrypt=false} 的接口会从全局解密中排除。
 * 未提供 {@link CryptoService} 实现或算法被动态关闭时按明文透传。
 * 解密后如 XSS 防护开启，还会对内容进行清理。
 *
 * @author YCH
 */
@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class DecryptRequestBodyAdvice extends RequestBodyAdviceAdapter {
    private final WebProperties webProperties;
    private final ObjectProvider<CryptoService> cryptoServiceProvider;

    @Override
    public boolean supports(MethodParameter methodParameter, Type targetType,
                            Class<? extends HttpMessageConverter<?>> converterType) {
        CryptoService cryptoService = cryptoServiceProvider.getIfAvailable();
        if (cryptoService == null) {
            return false;
        }
        Crypto crypto = AnnotationUtils.resolve(methodParameter, Crypto.class);
        if (crypto != null) {
            return crypto.decrypt() && cryptoService.enabled(crypto.algorithm());
        }
        return cryptoService.globalDecrypt() && cryptoService.enabled(cryptoService.defaultAlgorithm());
    }

    @Override
    public HttpInputMessage beforeBodyRead(HttpInputMessage inputMessage, MethodParameter parameter,
                                           Type targetType, Class<? extends HttpMessageConverter<?>> converterType) throws IOException {
        CryptoService cryptoService = cryptoServiceProvider.getIfAvailable();
        Crypto crypto = AnnotationUtils.resolve(parameter, Crypto.class);
        String algorithmName = crypto != null ? crypto.algorithm() : cryptoService.defaultAlgorithm();
        String ciphertext = new String(inputMessage.getBody().readAllBytes(), StandardCharsets.UTF_8);
        log.debug("请求解密: algorithm={}", algorithmName);
        String plaintext = cryptoService.decrypt(ciphertext, algorithmName);

        if (webProperties.getXss().isEnabled()) {
            plaintext = XssCleaner.clean(plaintext);
        }

        InputStream decryptedStream = new ByteArrayInputStream(plaintext.getBytes(StandardCharsets.UTF_8));
        return new HttpInputMessage() {
            @Override
            public InputStream getBody() {
                return decryptedStream;
            }

            @Override
            public HttpHeaders getHeaders() {
                return inputMessage.getHeaders();
            }
        };
    }
}
