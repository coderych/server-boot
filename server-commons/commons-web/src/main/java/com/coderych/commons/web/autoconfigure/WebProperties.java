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

    private boolean enabled = true;

    private Exception exception = new Exception();

    private Xss xss = new Xss();

    private Cors cors = new Cors();

    @Getter
    @Setter
    public static class Exception {
        private boolean enabled = true;
    }

    @Getter
    @Setter
    public static class Xss {
        private boolean enabled = true;

        private List<String> excludePaths = new ArrayList<>();

        private String mode = "clean";
    }

    @Getter
    @Setter
    public static class Cors {
        private boolean enabled = true;

        private String pathPattern = "/**";

        private List<String> allowedOrigins = new ArrayList<>();

        private List<String> allowedOriginPatterns = new ArrayList<>();

        private List<String> allowedMethods = new ArrayList<>();

        private List<String> allowedHeaders = new ArrayList<>();

        private List<String> exposedHeaders = new ArrayList<>();

        private Boolean allowCredentials;

        private Long maxAge;
    }
}
