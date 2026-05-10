package com.coderych.commons.mybatisflex.autoconfigure;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * MyBatis-Flex 模块配置属性，对应配置前缀 {@code commons.mybatis-flex}。
 *
 * @author YCH
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "commons.mybatis-flex")
public class MyBatisFlexProperties {

    private boolean enabled = true;

    private AutoFill autoFill = new AutoFill();

    /**
     * 自动填充配置，定义创建/更新时间和操作人字段的名称映射。
     */
    @Getter
    @Setter
    public static class AutoFill {
        private boolean enabled = true;

        private List<String> createTimeFields = List.of("create_time");

        private List<String> updateTimeFields = List.of("update_time");

        private List<String> createByFields = List.of("create_by");

        private List<String> updateByFields = List.of("update_by");
    }
}
