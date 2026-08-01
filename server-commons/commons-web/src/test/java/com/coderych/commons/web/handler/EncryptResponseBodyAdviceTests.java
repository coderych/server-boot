package com.coderych.commons.web.handler;

import com.coderych.commons.web.annotation.Crypto;
import com.coderych.commons.web.crypto.CryptoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EncryptResponseBodyAdviceTests {

    static class TestController {
        @Crypto(algorithm = "aes")
        public String annotated() {
            return null;
        }

        @Crypto(encrypt = false, algorithm = "aes")
        public String optOut() {
            return null;
        }

        public String plain() {
            return null;
        }
    }

    @Test
    void annotationModeOnlyEncryptsAnnotatedMethods() {
        EncryptResponseBodyAdvice advice = new EncryptResponseBodyAdvice(provider(annotationService()));

        assertTrue(advice.supports(method("annotated"), null));
        assertFalse(advice.supports(method("plain"), null));
        assertFalse(advice.supports(method("optOut"), null));
    }

    @Test
    void globalEncryptModeEncryptsAllMethodsExceptOptOut() {
        EncryptResponseBodyAdvice advice = new EncryptResponseBodyAdvice(provider(globalService()));

        assertTrue(advice.supports(method("annotated"), null));
        assertTrue(advice.supports(method("plain"), null));
        assertFalse(advice.supports(method("optOut"), null));
    }

    @Test
    void globalDecryptOnlyDoesNotEncryptPlainMethods() {
        EncryptResponseBodyAdvice advice = new EncryptResponseBodyAdvice(provider(decryptOnlyService()));

        assertTrue(advice.supports(method("annotated"), null));
        assertFalse(advice.supports(method("plain"), null));
    }

    @Test
    void disabledAlgorithmSkipsEncryption() {
        EncryptResponseBodyAdvice advice = new EncryptResponseBodyAdvice(provider(disabledService()));

        assertFalse(advice.supports(method("annotated"), null));
        assertFalse(advice.supports(method("plain"), null));
    }

    @Test
    void withoutCryptoServiceSkipsAll() {
        EncryptResponseBodyAdvice advice = new EncryptResponseBodyAdvice(provider(null));

        assertFalse(advice.supports(method("annotated"), null));
        assertFalse(advice.supports(method("plain"), null));
    }

    @Test
    void beforeBodyWriteUsesAnnotationAlgorithm() {
        EncryptResponseBodyAdvice advice = new EncryptResponseBodyAdvice(provider(annotationService()));

        Object result = advice.beforeBodyWrite("hello", method("annotated"), MediaType.APPLICATION_JSON,
                null, new ServletServerHttpRequest(new MockHttpServletRequest()),
                new ServletServerHttpResponse(new MockHttpServletResponse()));

        assertEquals("aes:hello", result);
    }

    @Test
    void beforeBodyWriteUsesDefaultAlgorithmInGlobalMode() {
        EncryptResponseBodyAdvice advice = new EncryptResponseBodyAdvice(provider(globalService()));

        ServletServerHttpResponse response = new ServletServerHttpResponse(new MockHttpServletResponse());
        Object result = advice.beforeBodyWrite("hello", method("plain"), MediaType.APPLICATION_JSON,
                null, new ServletServerHttpRequest(new MockHttpServletRequest()), response);

        assertEquals("default-aes:hello", result);
        assertEquals("text/plain;charset=UTF-8", response.getHeaders().getContentType().toString());
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

    private CryptoService annotationService() {
        return new CryptoService() {
            @Override
            public boolean enabled(String algorithm) {
                return true;
            }

            @Override
            public String encrypt(String plaintext, String algorithm) {
                return algorithm + ":" + plaintext;
            }

            @Override
            public String decrypt(String ciphertext, String algorithm) {
                return ciphertext;
            }
        };
    }

    private CryptoService globalService() {
        return new CryptoService() {
            @Override
            public boolean globalEncrypt() {
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
                return algorithm + ":" + plaintext;
            }

            @Override
            public String decrypt(String ciphertext, String algorithm) {
                return ciphertext;
            }
        };
    }

    private CryptoService decryptOnlyService() {
        return new CryptoService() {
            @Override
            public boolean globalDecrypt() {
                return true;
            }

            @Override
            public boolean enabled(String algorithm) {
                return true;
            }

            @Override
            public String encrypt(String plaintext, String algorithm) {
                return algorithm + ":" + plaintext;
            }

            @Override
            public String decrypt(String ciphertext, String algorithm) {
                return ciphertext;
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
