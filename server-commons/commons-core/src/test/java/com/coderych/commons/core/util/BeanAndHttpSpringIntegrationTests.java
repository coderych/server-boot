package com.coderych.commons.core.util;

import com.coderych.commons.core.autoconfigure.CoreAutoConfiguration;
import com.coderych.commons.core.util.web.HttpUtils;
import io.github.linpeilie.Converter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.web.client.RestClient;

import static org.junit.jupiter.api.Assertions.assertSame;

class BeanAndHttpSpringIntegrationTests {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(CoreAutoConfiguration.class))
            .withBean(Converter.class, Converter::new)
            .withBean(RestClient.Builder.class, RestClient::builder);

    @AfterEach
    void tearDown() {
        BEAN.reset();
        HttpUtils.reset();
    }

    @Test
    void shouldReuseSpringManagedBeans() {
        contextRunner.run(context -> {
            assertSame(context.getBean(Converter.class), BEAN.getConverter());
            assertSame(context.getBean(RestClient.Builder.class).build().getClass(), HttpUtils.getRestClient().getClass());
        });
    }
}
