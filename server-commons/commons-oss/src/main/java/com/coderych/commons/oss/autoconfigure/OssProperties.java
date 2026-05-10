package com.coderych.commons.oss.autoconfigure;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 对象存储模块配置属性，对应配置前缀 {@code commons.oss}。
 *
 * @author YCH
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "commons.oss")
public class OssProperties {

    private boolean enabled = true;
}
