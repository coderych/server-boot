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

    /**
     * 是否启用 MyBatis-Flex 模块。
     */
    private boolean enabled = true;

    /**
     * 自动填充配置。
     */
    private AutoFill autoFill = new AutoFill();

    /**
     * 自动填充配置，定义创建/更新时间和操作人字段的名称映射。
     */
    @Getter
    @Setter
    public static class AutoFill {
        /**
         * 是否启用自动填充。
         */
        private boolean enabled = true;

        /**
         * 创建时间字段名称。
         */
        private List<String> createTimeFields = List.of("create_time");

        /**
         * 更新时间字段名称。
         */
        private List<String> updateTimeFields = List.of("update_time");

        /**
         * 创建人字段名称。
         */
        private List<String> createByFields = List.of("create_by");

        /**
         * 更新人字段名称。
         */
        private List<String> updateByFields = List.of("update_by");
    }
}
