package com.coderych.commons.log.autoconfigure;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LogPropertiesTests {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(LogAutoConfiguration.class));

    @Test
    void shouldHaveCorrectDefaults() {
        contextRunner.run(context -> {
            LogProperties properties = context.getBean(LogProperties.class);

            assertTrue(properties.isEnabled());
            assertTrue(properties.isIncludeArgs());
            assertTrue(properties.isIncludeResult());
            assertEquals(2000, properties.getMaxLength());

            List<String> fields = properties.getSensitiveFields();
            assertNotNull(fields);
            assertEquals(10, fields.size());
            assertTrue(fields.contains("password"));
            assertTrue(fields.contains("oldPassword"));
            assertTrue(fields.contains("newPassword"));
            assertTrue(fields.contains("confirmPassword"));
            assertTrue(fields.contains("token"));
            assertTrue(fields.contains("accessToken"));
            assertTrue(fields.contains("refreshToken"));
            assertTrue(fields.contains("secret"));
            assertTrue(fields.contains("phone"));
            assertTrue(fields.contains("idCard"));
        });
    }

    @Test
    void shouldBindCustomValues() {
        contextRunner
                .withPropertyValues(
                        "commons.log.include-args=false",
                        "commons.log.include-result=false",
                        "commons.log.max-length=500"
                )
                .run(context -> {
                    LogProperties properties = context.getBean(LogProperties.class);

                    assertFalse(properties.isIncludeArgs());
                    assertFalse(properties.isIncludeResult());
                    assertEquals(500, properties.getMaxLength());
                });
    }

    @Test
    void shouldAllowModificationOfSensitiveFields() {
        LogProperties properties = new LogProperties();
        assertNotNull(properties.getSensitiveFields());

        properties.getSensitiveFields().add("customField");
        assertTrue(properties.getSensitiveFields().contains("customField"));
        assertEquals(11, properties.getSensitiveFields().size());
    }

    @Test
    void shouldSettersWork() {
        LogProperties properties = new LogProperties();

        properties.setEnabled(false);
        assertFalse(properties.isEnabled());

        properties.setIncludeArgs(false);
        assertFalse(properties.isIncludeArgs());

        properties.setIncludeResult(false);
        assertFalse(properties.isIncludeResult());

        properties.setMaxLength(500);
        assertEquals(500, properties.getMaxLength());

        properties.setSensitiveFields(List.of("a", "b"));
        assertEquals(2, properties.getSensitiveFields().size());
    }
}
