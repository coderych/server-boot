package com.coderych.commons.web.autoconfigure;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * Web 模块配置属性，对应配置前缀 {@code commons.web}。
 * <p>包含异常处理、XSS 防护和 CORS 三个子配置（接口加解密由业务实现 {@code CryptoService} 决定，不再读取配置）。</p>
 *
 * @author YCH
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "commons.web")
public class WebProperties {

    /**
     * 是否启用 Web 模块。
     */
    private boolean enabled = true;

    /**
     * 全局异常处理配置。
     */
    private Exception exception = new Exception();

    /**
     * XSS 防护配置。
     */
    private Xss xss = new Xss();

    /**
     * 跨域配置。
     */
    private Cors cors = new Cors();

    @Getter
    @Setter
    public static class Exception {
        /**
         * 是否启用异常处理。
         */
        private boolean enabled = true;
    }

    @Getter
    @Setter
    public static class Xss {
        /**
         * 是否启用 XSS 防护。
         */
        private boolean enabled = true;

        /**
         * XSS 防护排除路径。
         */
        private List<String> excludePaths = new ArrayList<>();

        /**
         * XSS 清理模式。
         */
        private String mode = "clean";
    }

    @Getter
    @Setter
    public static class Cors {
        /**
         * 是否启用跨域处理。
         */
        private boolean enabled = true;

        /**
         * 跨域匹配路径。
         */
        private String pathPattern = "/**";

        /**
         * 允许的来源列表。
         */
        private List<String> allowedOrigins = new ArrayList<>();

        /**
         * 允许的来源模式列表。
         */
        private List<String> allowedOriginPatterns = new ArrayList<>();

        /**
         * 允许的请求方法列表。
         */
        private List<String> allowedMethods = new ArrayList<>();

        /**
         * 允许的请求头列表。
         */
        private List<String> allowedHeaders = new ArrayList<>();

        /**
         * 允许暴露的响应头列表。
         */
        private List<String> exposedHeaders = new ArrayList<>();

        /**
         * 是否允许携带凭证。
         */
        private Boolean allowCredentials;

        /**
         * 预检请求缓存时间。
         */
        private Long maxAge;
    }
}
