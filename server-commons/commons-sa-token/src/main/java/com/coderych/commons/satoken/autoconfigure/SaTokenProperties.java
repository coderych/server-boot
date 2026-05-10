package com.coderych.commons.satoken.autoconfigure;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Sa-Token 模块配置属性，对应配置前缀 {@code commons.sa-token}。
 *
 * @author YCH
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "commons.sa-token")
public class SaTokenProperties {

    private boolean enabled = true;

    private String[] include = new String[]{"/**"};

    private String[] exclude = new String[]{};

    private String[] superAdmins = new String[]{};
}
