package com.coderych.commons.web.handler;

import com.coderych.commons.web.annotation.Crypto;
import com.coderych.commons.web.autoconfigure.WebProperties;
import com.coderych.commons.web.crypto.CryptoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.mock.http.MockHttpInputMessage;

import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DecryptRequestBodyAdviceTests {

    static class TestController {
        @Crypto(algorithm = "aes")
        public String annotated() {
            return null;
        }

        @Crypto(decrypt = false, algorithm = "aes")
        public String optOut() {
            return null;
        }

        public String plain() {
            return null;
        }
    }

    private final WebProperties webProperties = xssDisabledProperties();

    @Test
    void annotationModeOnlyDecryptsAnnotatedMethods() {
        DecryptRequestBodyAdvice advice = new DecryptRequestBodyAdvice(webProperties, provider(annotationService()));

        assertTrue(advice.supports(method("annotated"), null, null));
        assertFalse(advice.supports(method("plain"), null, null));
        assertFalse(advice.supports(method("optOut"), null, null));
    }

    @Test
    void globalDecryptModeDecryptsAllMethodsExceptOptOut() {
        DecryptRequestBodyAdvice advice = new DecryptRequestBodyAdvice(webProperties, provider(globalService()));

        assertTrue(advice.supports(method("annotated"), null, null));
        assertTrue(advice.supports(method("plain"), null, null));
        assertFalse(advice.supports(method("optOut"), null, null));
    }

    @Test
    void globalEncryptOnlyDoesNotDecryptPlainMethods() {
        DecryptRequestBodyAdvice advice = new DecryptRequestBodyAdvice(webProperties, provider(encryptOnlyService()));

        assertTrue(advice.supports(method("annotated"), null, null));
        assertFalse(advice.supports(method("plain"), null, null));
    }

    @Test
    void disabledAlgorithmSkipsDecryption() {
        DecryptRequestBodyAdvice advice = new DecryptRequestBodyAdvice(webProperties, provider(disabledService()));

        assertFalse(advice.supports(method("annotated"), null, null));
        assertFalse(advice.supports(method("plain"), null, null));
    }

    @Test
    void withoutCryptoServiceSkipsAll() {
        DecryptRequestBodyAdvice advice = new DecryptRequestBodyAdvice(webProperties, provider(null));

        assertFalse(advice.supports(method("annotated"), null, null));
        assertFalse(advice.supports(method("plain"), null, null));
    }

    @Test
    void beforeBodyReadUsesAnnotationAlgorithm() throws Exception {
        DecryptRequestBodyAdvice advice = new DecryptRequestBodyAdvice(webProperties, provider(annotationService()));

        HttpInputMessage result = advice.beforeBodyRead(
                new MockHttpInputMessage("cipher".getBytes(StandardCharsets.UTF_8)),
                method("annotated"), null, null);

        assertEquals("aes:cipher", new String(result.getBody().readAllBytes(), StandardCharsets.UTF_8));
    }

    @Test
    void beforeBodyReadUsesDefaultAlgorithmInGlobalMode() throws Exception {
        DecryptRequestBodyAdvice advice = new DecryptRequestBodyAdvice(webProperties, provider(globalService()));

        HttpInputMessage result = advice.beforeBodyRead(
                new MockHttpInputMessage("cipher".getBytes(StandardCharsets.UTF_8)),
                method("plain"), null, null);

        assertEquals("default-aes:cipher", new String(result.getBody().readAllBytes(), StandardCharsets.UTF_8));
    }

    private static MethodParameter method(String name) {
        try {
            return MethodParameter.forExecutable(TestController.class.getMethod(name), -1);
        } catch (NoSuchMethodException exception) {
            throw new IllegalStateException(exception);
        }
    }

    private static ObjectProvider<CryptoService> provider(CryptoService service) {
        return new ObjectProvider<>() {
            @Override
            public Stream<CryptoService> stream() {
                return service == null ? Stream.empty() : Stream.of(service);
            }
        };
    }

    private WebProperties xssDisabledProperties() {
        WebProperties properties = new WebProperties();
        properties.getXss().setEnabled(false);
        return properties;
    }

    private CryptoService annotationService() {
        return new CryptoService() {
            @Override
            public boolean enabled(String algorithm) {
                return true;
            }

            @Override
            public String encrypt(String plaintext, String algorithm) {
                return plaintext;
            }

            @Override
            public String decrypt(String ciphertext, String algorithm) {
                return algorithm + ":" + ciphertext;
            }
        };
    }

    private CryptoService globalService() {
        return new CryptoService() {
            @Override
            public boolean globalDecrypt() {
                return true;
            }

            @Override
            public String defaultAlgorithm() {
                return "default-aes";
            }

            @Override
            public boolean enabled(String algorithm) {
                return true;
            }

            @Override
            public String encrypt(String plaintext, String algorithm) {
                return plaintext;
            }

            @Override
            public String decrypt(String ciphertext, String algorithm) {
                return algorithm + ":" + ciphertext;
            }
        };
    }

    private CryptoService encryptOnlyService() {
        return new CryptoService() {
            @Override
            public boolean globalEncrypt() {
                return true;
            }

            @Override
            public boolean enabled(String algorithm) {
                return true;
            }

            @Override
            public String encrypt(String plaintext, String algorithm) {
                return plaintext;
            }

            @Override
            public String decrypt(String ciphertext, String algorithm) {
                return algorithm + ":" + ciphertext;
            }
        };
    }

    private CryptoService disabledService() {
        return new CryptoService() {
            @Override
            public boolean enabled(String algorithm) {
                return false;
            }

            @Override
            public String encrypt(String plaintext, String algorithm) {
                return plaintext;
            }

            @Override
            public String decrypt(String ciphertext, String algorithm) {
                return ciphertext;
            }
        };
    }
}
