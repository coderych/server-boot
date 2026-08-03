package com.coderych.commons.core.autoconfigure;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 核心模块配置属性，前缀 {@code commons.core}。
 *
 * @author YCH
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "commons.core")
public class CoreProperties {

    /**
     * 是否启用核心模块。
     */
    private boolean enabled = true;
}
