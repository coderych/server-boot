package com.coderych.commons.log.autoconfigure;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * 日志模块配置属性。
 * <p>配置前缀为 {@code commons.log}，支持控制日志开关、参数/结果采集、序列化长度限制及敏感字段脱敏。</p>
 *
 * @author YCH
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "commons.log")
public class LogProperties {

    private boolean enabled = true;

    private boolean includeArgs = true;

    private boolean includeResult = true;

    private int maxLength = 2000;

    private List<String> sensitiveFields = new ArrayList<>(List.of(
            "password",
            "oldPassword",
            "newPassword",
            "confirmPassword",
            "token",
            "accessToken",
            "refreshToken",
            "secret",
            "phone",
            "idCard"
    ));
}
