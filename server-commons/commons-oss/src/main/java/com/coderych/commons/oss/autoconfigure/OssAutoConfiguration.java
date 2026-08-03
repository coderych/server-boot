package com.coderych.commons.oss.autoconfigure;

import lombok.extern.slf4j.Slf4j;
import org.dromara.x.file.storage.spring.EnableFileStorage;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * 对象存储（OSS）模块自动配置类。
 * <p>当 {@code commons.oss.enabled=true}（默认）时自动生效，注册 OSS 相关 Bean。</p>
 *
 * @author YCH
 */
@Slf4j
@AutoConfiguration
@EnableConfigurationProperties(OssProperties.class)
@ConditionalOnProperty(prefix = "commons.oss", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableFileStorage
public class OssAutoConfiguration {
}
