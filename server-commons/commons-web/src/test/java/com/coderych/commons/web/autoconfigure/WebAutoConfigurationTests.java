package com.coderych.commons.web.autoconfigure;

import com.coderych.commons.web.handler.DecryptRequestBodyAdvice;
import com.coderych.commons.web.handler.EncryptResponseBodyAdvice;
import com.coderych.commons.web.handler.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import tools.jackson.databind.json.JsonMapper;

import static org.assertj.core.api.Assertions.assertThat;

class WebAutoConfigurationTests {

    private final WebApplicationContextRunner contextRunner = new WebApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(WebAutoConfiguration.class))
            .withBean("jsonMapper", JsonMapper.class, JsonMapper::new);

    @Test
    void shouldRegisterAllBeansByDefault() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(GlobalExceptionHandler.class);
            assertThat(context).hasSingleBean(DecryptRequestBodyAdvice.class);
            assertThat(context).hasSingleBean(EncryptResponseBodyAdvice.class);
            assertThat(context).hasSingleBean(FilterRegistrationBean.class);
            assertThat(context).hasSingleBean(WebMvcConfigurer.class);
        });
    }

    @Test
    void shouldNotRegisterBeansWhenModuleDisabled() {
        contextRunner
                .withPropertyValues("commons.web.enabled=false")
                .run(context -> {
                    assertThat(context).doesNotHaveBean(GlobalExceptionHandler.class);
                    assertThat(context).doesNotHaveBean(DecryptRequestBodyAdvice.class);
                    assertThat(context).doesNotHaveBean(EncryptResponseBodyAdvice.class);
                    assertThat(context).doesNotHaveBean(FilterRegistrationBean.class);
                    assertThat(context).doesNotHaveBean(WebMvcConfigurer.class);
                });
    }

    @Test
    void shouldNotRegisterExceptionHandlerWhenDisabled() {
        contextRunner
                .withPropertyValues("commons.web.exception.enabled=false")
                .run(context -> {
                    assertThat(context).doesNotHaveBean(GlobalExceptionHandler.class);
                    assertThat(context).hasSingleBean(DecryptRequestBodyAdvice.class);
                    assertThat(context).hasSingleBean(EncryptResponseBodyAdvice.class);
                });
    }

    @Test
    void shouldAlwaysRegisterCryptoBeansButDisableInternally() {
        contextRunner
                .withPropertyValues("commons.web.crypto.enabled=false")
                .run(context -> {
                    assertThat(context).hasSingleBean(DecryptRequestBodyAdvice.class);
                    assertThat(context).hasSingleBean(EncryptResponseBodyAdvice.class);
                    assertThat(context).hasSingleBean(GlobalExceptionHandler.class);
                });
    }

    @Test
    void shouldNotRegisterXssFilterWhenDisabled() {
        contextRunner
                .withPropertyValues("commons.web.xss.enabled=false")
                .run(context -> {
                    assertThat(context).doesNotHaveBean(FilterRegistrationBean.class);
                    assertThat(context).hasSingleBean(GlobalExceptionHandler.class);
                });
    }

    @Test
    void shouldNotRegisterCorsConfigurerWhenDisabled() {
        contextRunner
                .withPropertyValues("commons.web.cors.enabled=false")
                .run(context -> {
                    assertThat(context).doesNotHaveBean(WebMvcConfigurer.class);
                    assertThat(context).hasSingleBean(GlobalExceptionHandler.class);
                });
    }

    @Test
    void shouldBindWebProperties() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(WebProperties.class);
            WebProperties properties = context.getBean(WebProperties.class);
            assertThat(properties.isEnabled()).isTrue();
            assertThat(properties.getException().isEnabled()).isTrue();
            assertThat(properties.getCrypto().isEnabled()).isTrue();
            assertThat(properties.getXss().isEnabled()).isTrue();
            assertThat(properties.getCors().isEnabled()).isTrue();
        });
    }

    @Test
    void shouldBindCustomProperties() {
        contextRunner
                .withPropertyValues(
                        "commons.web.exception.enabled=false",
                        "commons.web.xss.mode=escape",
                        "commons.web.cors.path-pattern=/api/**"
                )
                .run(context -> {
                    WebProperties properties = context.getBean(WebProperties.class);
                    assertThat(properties.getException().isEnabled()).isFalse();
                    assertThat(properties.getXss().getMode()).isEqualTo("escape");
                    assertThat(properties.getCors().getPathPattern()).isEqualTo("/api/**");
                });
    }
}
