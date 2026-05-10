package com.coderych.commons.mybatisflex.autoconfigure;

import com.coderych.commons.mybatisflex.service.DatabaseMetadataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;

/**
 * MyBatis-Flex 模块自动配置类，注册数据库元数据服务等基础设施 Bean。
 * <p>可通过 {@code commons.mybatis-flex.enabled=false} 禁用整个模块。</p>
 *
 * @author YCH
 */
@Slf4j
@AutoConfiguration
@EnableConfigurationProperties(MyBatisFlexProperties.class)
@ConditionalOnProperty(prefix = "commons.mybatis-flex", name = "enabled", havingValue = "true", matchIfMissing = true)
public class MyBatisFlexAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public DatabaseMetadataService databaseMetadataService(DataSource dataSource) {
        log.info(">>>>>>>>> Bean: databaseMetadataService —— 注册数据库元数据服务");
        return new DatabaseMetadataService(dataSource);
    }

}
