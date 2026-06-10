package com.coderych.commons.web.autoconfigure;

import com.coderych.commons.web.handler.GlobalExceptionHandler;
import com.coderych.commons.web.handler.DecryptRequestBodyAdvice;
import com.coderych.commons.web.handler.EncryptResponseBodyAdvice;
import com.coderych.commons.web.handler.XssFilter;
import com.coderych.commons.web.util.Cryptos;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web 模块自动配置类。
 * <p>按需注册全局异常处理、请求/响应加解密 Advice、XSS 过滤器和 CORS 配置器，各子功能均可通过配置独立开关。</p>
 *
 * @author YCH
 */
@Slf4j
@AutoConfiguration
@EnableConfigurationProperties(WebProperties.class)
@ConditionalOnWebApplication
@ConditionalOnProperty(prefix = "commons.web", name = "enabled", havingValue = "true", matchIfMissing = true)
public class WebAutoConfiguration {
    @Bean
    public SmartInitializingSingleton webModuleInitializer(WebProperties webProperties) {
        return () -> {
            log.info(">>>>>>>>> Bean: webModuleInitializer —— 初始化 Web 模块（加解密工具）");
            Cryptos.init(webProperties);
        };
    }


    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "commons.web.exception", name = "enabled", havingValue = "true", matchIfMissing = true)
    public GlobalExceptionHandler globalExceptionHandler() {
        log.info(">>>>>>>>> Bean: globalExceptionHandler —— 注册全局异常处理器");
        return new GlobalExceptionHandler();
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "commons.web.crypto", name = "enabled", havingValue = "true", matchIfMissing = true)
    public DecryptRequestBodyAdvice decryptRequestBodyAdvice(WebProperties webProperties) {
        log.info(">>>>>>>>> Bean: decryptRequestBodyAdvice —— 注册请求体解密 Advice");
        return new DecryptRequestBodyAdvice(webProperties);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "commons.web.crypto", name = "enabled", havingValue = "true", matchIfMissing = true)
    public EncryptResponseBodyAdvice encryptResponseBodyAdvice(WebProperties webProperties) {
        log.info(">>>>>>>>> Bean: encryptResponseBodyAdvice —— 注册响应体加密 Advice");
        return new EncryptResponseBodyAdvice(webProperties);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "commons.web.xss", name = "enabled", havingValue = "true", matchIfMissing = true)
    public FilterRegistrationBean<XssFilter> xssFilterRegistration(WebProperties webProperties) {
        log.info(">>>>>>>>> Bean: xssFilterRegistration —— 注册 XSS 过滤器");
        FilterRegistrationBean<XssFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new XssFilter(webProperties));
        registration.addUrlPatterns("/*");
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE + 10);
        return registration;
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "commons.web.cors", name = "enabled", havingValue = "true", matchIfMissing = true)
    public WebMvcConfigurer corsWebMvcConfigurer(WebProperties webProperties) {
        log.info(">>>>>>>>> Bean: corsWebMvcConfigurer —— 注册 CORS 跨域配置");
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                WebProperties.Cors cors = webProperties.getCors();
                var registration = registry.addMapping(cors.getPathPattern());

                applyIfNonEmpty(cors.getAllowedOrigins(), registration::allowedOrigins);
                applyIfNonEmpty(cors.getAllowedOriginPatterns(), registration::allowedOriginPatterns);
                applyIfNonEmpty(cors.getAllowedMethods(), registration::allowedMethods);
                applyIfNonEmpty(cors.getAllowedHeaders(), registration::allowedHeaders);
                applyIfNonEmpty(cors.getExposedHeaders(), registration::exposedHeaders);
                if (cors.getAllowCredentials() != null) {
                    registration.allowCredentials(cors.getAllowCredentials());
                }
                if (cors.getMaxAge() != null) {
                    registration.maxAge(cors.getMaxAge());
                }
            }

            private void applyIfNonEmpty(java.util.List<String> values, java.util.function.Consumer<String[]> consumer) {
                if (values != null && !values.isEmpty()) {
                    consumer.accept(values.toArray(new String[0]));
                }
            }
        };
    }
}
