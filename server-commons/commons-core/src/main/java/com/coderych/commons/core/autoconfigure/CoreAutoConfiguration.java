package com.coderych.commons.core.autoconfigure;

import com.coderych.commons.core.util.BEAN;
import com.coderych.commons.core.util.JSON;
import com.coderych.commons.core.util.web.HttpUtils;
import io.github.linpeilie.Converter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.json.JsonMapper;

/**
 * 核心模块自动配置类，初始化 JSON、对象映射和 HTTP 工具。
 *
 * @author YCH
 */
@Slf4j
@AutoConfiguration(afterName = "org.springframework.boot.jackson.autoconfigure.JacksonAutoConfiguration")
@EnableConfigurationProperties(CoreProperties.class)
@ConditionalOnProperty(prefix = "commons.core", name = "enabled", havingValue = "true", matchIfMissing = true)
public class CoreAutoConfiguration {
    @Bean
    public SmartInitializingSingleton coreModuleInitializer(ObjectProvider<JsonMapper> jsonMapperProvider,
                                                            ObjectProvider<Converter> converterProvider,
                                                            ObjectProvider<RestClient.Builder> restClientBuilderProvider) {
        return () -> {
            log.info(">>>>>>>>> Bean: coreModuleInitializer —— 初始化核心模块（JSON、对象映射、HTTP 工具）");
            jsonMapperProvider.ifAvailable(JSON::init);
            converterProvider.ifAvailable(BEAN::init);
            restClientBuilderProvider.ifAvailable(builder -> HttpUtils.init(builder.build()));
        };
    }
}
