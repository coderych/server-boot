package com.coderych.commons.satoken.autoconfigure;

import cn.dev33.satoken.config.SaTokenConfig;
import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.filter.SaServletFilter;
import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.stp.StpUtil;
import com.coderych.commons.core.model.R;
import com.coderych.commons.satoken.core.LoginUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Sa-Token 认证授权模块自动配置类。
 * <p>注册全局登录拦截器、Servlet 过滤器（安全响应头）以及 {@link LoginUser} 初始化器。</p>
 *
 * @author YCH
 */
@Slf4j
@AutoConfiguration
@EnableConfigurationProperties(SaTokenProperties.class)
@ConditionalOnProperty(prefix = "commons.sa-token", name = "enabled", havingValue = "true", matchIfMissing = true)
public class SaTokenAutoConfiguration {

    @Bean
    public WebMvcConfigurer saTokenWebMvcConfigurer(SaTokenProperties saTokenProperties) {
        log.info(">>>>>>>>> Bean: saTokenWebMvcConfigurer —— 注册 Sa-Token 登录拦截器");
        return new WebMvcConfigurer() {
            @Override
            public void addInterceptors(InterceptorRegistry registry) {
                registry.addInterceptor(new SaInterceptor(handle -> {
                    StpUtil.checkLogin();
                })).addPathPatterns("/**").excludePathPatterns(saTokenProperties.getExclude());
            }
        };
    }

    @Bean
    public SaServletFilter saServletFilter(SaTokenProperties saTokenProperties) {
        log.info(">>>>>>>>> Bean: saServletFilter —— 注册 Sa-Token Servlet 过滤器（安全响应头）");
        return new SaServletFilter()
                .addInclude(saTokenProperties.getInclude())
                .addExclude(saTokenProperties.getExclude())
                .setAuth(auth -> {
                })
                .setError(error -> R.fail(error.getMessage()))
                .setBeforeAuth(beforeAuth -> {
                    SaHolder.getResponse()
                            .setServer("app")
                            .setHeader("X-Frame-Options", "SAMEORIGIN")
                            .setHeader("X-XSS-Protection", "1; mode=block")
                            .setHeader("X-Content-Type-Options", "nosniff");
                });
    }

    @Bean
    public SmartInitializingSingleton satokenInitializer(SaTokenConfig saTokenConfig, SaTokenProperties saTokenProperties) {
        return () -> {
            log.info(">>>>>>>>> Bean: satokenInitializer —— 初始化 Sa-Token LoginUser");
            LoginUser.init(saTokenConfig.getTokenName(), saTokenProperties.getSuperAdmins());
        };
    }
}
