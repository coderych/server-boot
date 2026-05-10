package com.coderych.commons.log.autoconfigure;

import com.coderych.commons.log.aspect.AutoLogAspect;
import com.coderych.commons.log.support.ParameterSerializer;
import com.coderych.commons.log.support.SensitiveValueMasker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * 日志模块自动配置类。
 * <p>注册 {@link SensitiveValueMasker}、{@link ParameterSerializer} 和 {@link AutoLogAspect} 三个核心 Bean，
 * 可通过 {@code commons.log.enabled=false} 关闭整个自动日志功能。</p>
 *
 * @author YCH
 */
@Slf4j
@AutoConfiguration
@EnableConfigurationProperties(LogProperties.class)
@ConditionalOnProperty(prefix = "commons.log", name = "enabled", havingValue = "true", matchIfMissing = true)
public class LogAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public SensitiveValueMasker sensitiveValueMasker(LogProperties properties) {
        log.info(">>>>>>>>> Bean: sensitiveValueMasker —— 注册敏感值脱敏器");
        return new SensitiveValueMasker(properties);
    }

    @Bean
    @ConditionalOnMissingBean
    public ParameterSerializer parameterSerializer(LogProperties properties, SensitiveValueMasker sensitiveValueMasker) {
        log.info(">>>>>>>>> Bean: parameterSerializer —— 注册参数序列化器");
        return new ParameterSerializer(properties, sensitiveValueMasker);
    }

    @Bean
    @ConditionalOnMissingBean
    public AutoLogAspect autoLogAspect(LogProperties properties, ParameterSerializer parameterSerializer) {
        log.info(">>>>>>>>> Bean: autoLogAspect —— 注册自动日志切面");
        return new AutoLogAspect(properties, parameterSerializer);
    }
}
