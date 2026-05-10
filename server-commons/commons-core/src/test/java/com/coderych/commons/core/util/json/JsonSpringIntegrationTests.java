package com.coderych.commons.core.util.json;

import com.coderych.commons.core.autoconfigure.CoreAutoConfiguration;
import com.coderych.commons.core.util.JSON;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.jackson.autoconfigure.JacksonAutoConfiguration;
import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.json.JsonMapper;

import static org.junit.jupiter.api.Assertions.assertSame;

class JsonSpringIntegrationTests {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(JacksonAutoConfiguration.class, CoreAutoConfiguration.class))
            .withBean(JsonMapperBuilderCustomizer.class,
                    () -> builder -> builder.enable(SerializationFeature.INDENT_OUTPUT));

    @AfterEach
    void tearDown() {
        JSON.reset();
    }

    @Test
    void shouldReuseSpringManagedJsonMapper() {
        contextRunner.run(context -> {
            JsonMapper jsonMapper = context.getBean(JsonMapper.class);

            assertSame(jsonMapper, JSON.getJsonMapper());
        });
    }
}
