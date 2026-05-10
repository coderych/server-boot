package com.coderych.commons.core.util.spring;

import cn.hutool.extra.spring.SpringUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.core.env.Environment;

/**
 * Spring 容器工具类，扩展 Hutool {@link SpringUtil}，增加配置读取和环境判断。
 *
 * @author YCH
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SpringUtils extends SpringUtil {

    public static String getProperty(String key) {
        return getEnvironment().getProperty(key);
    }

    public static <T> T getProperty(String key, Class<T> targetType) {
        return getEnvironment().getProperty(key, targetType);
    }

    public static <T> T getProperty(String key, Class<T> targetType, T defaultValue) {
        return getEnvironment().getProperty(key, targetType, defaultValue);
    }

    public static boolean isActiveProfile(String profile) {
        return getEnvironment().matchesProfiles(profile);
    }

    private static Environment getEnvironment() {
        return getApplicationContext().getEnvironment();
    }
}
