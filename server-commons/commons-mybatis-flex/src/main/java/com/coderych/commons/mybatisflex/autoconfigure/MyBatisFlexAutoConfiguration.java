package com.coderych.commons.mybatisflex.autoconfigure;

import com.coderych.commons.mybatisflex.model.BaseEntity;
import com.coderych.commons.mybatisflex.aspect.CrudApiAspect;
import com.coderych.commons.mybatisflex.service.DatabaseMetadataService;
import com.coderych.commons.satoken.core.LoginUser;
import com.mybatisflex.core.audit.AuditManager;
import com.mybatisflex.core.audit.ConsoleMessageCollector;
import com.mybatisflex.core.audit.MessageCollector;
import com.mybatisflex.core.query.QueryColumnBehavior;
import com.mybatisflex.spring.boot.MyBatisFlexCustomizer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;
import java.time.LocalDateTime;

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
    static {
        MessageCollector collector = new ConsoleMessageCollector();
        AuditManager.setMessageCollector(collector);
        AuditManager.setAuditEnable(true);

        QueryColumnBehavior.setIgnoreFunction(QueryColumnBehavior.IGNORE_NULL);
        QueryColumnBehavior.setSmartConvertInToEquals(true);
        QueryColumnBehavior.setSmartConvertBetweenToLeOrGe(true);
    }

    @Bean
    @ConditionalOnMissingBean
    public DatabaseMetadataService databaseMetadataService(DataSource dataSource) {
        log.info(">>>>>>>>> Bean: databaseMetadataService —— 注册数据库元数据服务");
        return new DatabaseMetadataService(dataSource);
    }

    @Bean
    @ConditionalOnMissingBean
    public CrudApiAspect crudApiAspect() {
        log.info(">>>>>>>>> Bean: crudApiAspect —— 注册 CRUD 接口访问控制切面");
        return new CrudApiAspect();
    }

    @Bean
    @ConditionalOnMissingBean
    public MyBatisFlexCustomizer myBatisFlexCustomizer() {
        return globalConfig -> {
            log.info(">>>>>>>>> Bean: myBatisFlexCustomizer —— 注册 MyBatis-Flex 全局配置");
            // 插入监听
            globalConfig.registerInsertListener((object) -> {
                if (object instanceof BaseEntity baseEntity) {
                    String loginUsername = LoginUser.getLoginUsernameOrDefault("unknown");
                    LocalDateTime now = LocalDateTime.now();
                    baseEntity.setCreator(loginUsername);
                    baseEntity.setCreateTime(now);
                    baseEntity.setUpdater(loginUsername);
                    baseEntity.setUpdateTime(now);
                    baseEntity.setDeleted(0L);
                    baseEntity.setVersion(1);
                }
            });

            // 更新监听
            globalConfig.registerUpdateListener((object) -> {
                if (object instanceof BaseEntity baseEntity) {
                    String loginUsername = LoginUser.getLoginUsernameOrDefault("unknown");
                    baseEntity.setUpdater(loginUsername);
                    baseEntity.setUpdateTime(LocalDateTime.now());
                }
            });
        };
    }

}
