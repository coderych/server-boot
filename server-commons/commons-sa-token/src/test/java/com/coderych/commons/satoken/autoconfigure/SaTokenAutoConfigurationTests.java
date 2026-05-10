package com.coderych.commons.satoken.autoconfigure;

import cn.dev33.satoken.config.SaTokenConfig;
import cn.dev33.satoken.filter.SaServletFilter;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static org.assertj.core.api.Assertions.assertThat;

class SaTokenAutoConfigurationTests {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(SaTokenAutoConfiguration.class))
            .withBean(SaTokenConfig.class);

    @Test
    void whenEnabledByDefaultShouldLoadConfiguration() {
        contextRunner.run(context -> assertThat(context).hasSingleBean(SaTokenAutoConfiguration.class));
    }

    @Test
    void whenEnabledExplicitlyShouldLoadConfiguration() {
        contextRunner
                .withPropertyValues("commons.sa-token.enabled=true")
                .run(context -> assertThat(context).hasSingleBean(SaTokenAutoConfiguration.class));
    }

    @Test
    void whenDisabledShouldNotLoadConfiguration() {
        contextRunner
                .withPropertyValues("commons.sa-token.enabled=false")
                .run(context -> assertThat(context).doesNotHaveBean(SaTokenAutoConfiguration.class));
    }

    @Test
    void shouldRegisterWebMvcConfigurerBean() {
        contextRunner.run(context -> assertThat(context).hasSingleBean(WebMvcConfigurer.class));
    }

    @Test
    void shouldRegisterSaServletFilterBean() {
        contextRunner.run(context -> assertThat(context).hasSingleBean(SaServletFilter.class));
    }

    @Test
    void shouldRegisterSmartInitializingSingletonBean() {
        contextRunner.run(context -> assertThat(context).hasBean("satokenInitializer"));
    }

    @Test
    void shouldRegisterAllExpectedBeans() {
        contextRunner.run(context -> {
            assertThat(context).hasBean("saTokenWebMvcConfigurer");
            assertThat(context).hasBean("saServletFilter");
            assertThat(context).hasBean("satokenInitializer");
        });
    }
}
