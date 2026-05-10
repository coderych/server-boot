package com.coderych.commons.log.autoconfigure;

import com.coderych.commons.log.aspect.AutoLogAspect;
import com.coderych.commons.log.support.ParameterSerializer;
import com.coderych.commons.log.support.SensitiveValueMasker;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.junit.jupiter.api.Assertions.*;

class LogAutoConfigurationTests {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(LogAutoConfiguration.class));

    @Test
    void shouldRegisterAllBeansByDefault() {
        contextRunner.run(context -> {
            assertNotNull(context.getBean(SensitiveValueMasker.class));
            assertNotNull(context.getBean(ParameterSerializer.class));
            assertNotNull(context.getBean(AutoLogAspect.class));
        });
    }

    @Test
    void shouldNotRegisterBeansWhenDisabled() {
        contextRunner
                .withPropertyValues("commons.log.enabled=false")
                .run(context -> {
                    assertFalse(context.containsBean("sensitiveValueMasker"));
                    assertFalse(context.containsBean("parameterSerializer"));
                    assertFalse(context.containsBean("autoLogAspect"));
                });
    }

    @Test
    void shouldRespectConditionalOnMissingBean() {
        contextRunner
                .withBean(SensitiveValueMasker.class, () -> new SensitiveValueMasker(new LogProperties()))
                .withBean(ParameterSerializer.class, () -> {
                    LogProperties p = new LogProperties();
                    return new ParameterSerializer(p, new SensitiveValueMasker(p));
                })
                .run(context -> {
                    assertEquals(1, context.getBeanNamesForType(SensitiveValueMasker.class).length);
                    assertEquals(1, context.getBeanNamesForType(ParameterSerializer.class).length);
                });
    }

    @Test
    void shouldBindLogProperties() {
        contextRunner.run(context -> {
            LogProperties properties = context.getBean(LogProperties.class);
            assertNotNull(properties);
            assertTrue(properties.isEnabled());
        });
    }
}
