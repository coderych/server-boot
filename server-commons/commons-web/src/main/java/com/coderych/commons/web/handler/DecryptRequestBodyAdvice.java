package com.coderych.commons.web.handler;

import com.coderych.commons.core.util.spring.AnnotationUtils;
import com.coderych.commons.web.annotation.Crypto;
import com.coderych.commons.web.autoconfigure.WebProperties;
import com.coderych.commons.web.util.Cryptos;
import com.coderych.commons.web.util.XssCleaner;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
 * 全局请求体 Advice，对标注了 {@link Crypto} 注解且 {@code decrypt=true} 的接口自动解密请求体。
 * <p>解密后如 XSS 防护开启，还会对内容进行清理。</p>
 *
 * @author YCH
 */
@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class DecryptRequestBodyAdvice extends RequestBodyAdviceAdapter {
    private final WebProperties webProperties;

    @Override
    public boolean supports(MethodParameter methodParameter, Type targetType,
                            Class<? extends HttpMessageConverter<?>> converterType) {
        Crypto crypto = AnnotationUtils.resolve(methodParameter, Crypto.class);
        return crypto != null && crypto.decrypt();
    }

    @Override
    public HttpInputMessage beforeBodyRead(HttpInputMessage inputMessage, MethodParameter parameter,
                                           Type targetType, Class<? extends HttpMessageConverter<?>> converterType) throws IOException {
        Crypto crypto = AnnotationUtils.resolve(parameter, Crypto.class);
        String algorithmName = crypto.algorithm();
        String ciphertext = new String(inputMessage.getBody().readAllBytes(), StandardCharsets.UTF_8);
        log.debug("请求解密: algorithm={}", algorithmName);
        String plaintext = Cryptos.decrypt(ciphertext, algorithmName);

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
