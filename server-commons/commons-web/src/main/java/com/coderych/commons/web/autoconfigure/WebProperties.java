package com.coderych.commons.web.autoconfigure;

import com.coderych.commons.web.enums.CryptoAlgorithmType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Web 模块配置属性，对应配置前缀 {@code commons.web}。
 * <p>包含异常处理、加解密、XSS 防护和 CORS 四个子配置。</p>
 *
 * @author YCH
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "commons.web")
public class WebProperties {

    private boolean enabled = true;

    private Exception exception = new Exception();

    private Crypto crypto = new Crypto();

    private Xss xss = new Xss();

    private Cors cors = new Cors();

    @Getter
    @Setter
    public static class Exception {
        private boolean enabled = true;
    }

    @Getter
    @Setter
    public static class Crypto {
        private boolean enabled = true;

        private String defaultContentType = "text/plain;charset=UTF-8";

        private Map<String, CryptoAlgorithm> algorithms = new HashMap<>();
    }

    @Getter
    @Setter
    public static class CryptoAlgorithm {

        private CryptoAlgorithmType type;

        private String mode;

        private String padding;

        private String key;

        private String iv;

        private String privateKey;

        private String publicKey;

        private String encoding = "BASE64";
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
