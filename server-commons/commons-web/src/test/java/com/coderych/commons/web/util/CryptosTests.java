package com.coderych.commons.web.util;

import com.coderych.commons.web.autoconfigure.WebProperties;
import com.coderych.commons.web.enums.CryptoAlgorithmType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CryptosTests {

    @Test
    void shouldEncryptAndDecryptWithConfiguredAesAlgorithm() {
        WebProperties webProperties = new WebProperties();
        webProperties.getCrypto().getAlgorithms().put("aes", symmetricAlgorithm(CryptoAlgorithmType.AES));
        Cryptos.init(webProperties);

        String ciphertext = Cryptos.encrypt("hello", "aes");

        assertEquals("hello", Cryptos.decrypt(ciphertext, "aes"));
    }

    @Test
    void shouldEncryptAndDecryptWithConfiguredSm4Algorithm() {
        WebProperties webProperties = new WebProperties();
        webProperties.getCrypto().getAlgorithms().put("sm4", symmetricAlgorithm(CryptoAlgorithmType.SM4));
        Cryptos.init(webProperties);

        String ciphertext = Cryptos.encrypt("hello", "sm4");

        assertEquals("hello", Cryptos.decrypt(ciphertext, "sm4"));
    }

    @Test
    void shouldEncryptAndDecryptWithConfiguredRsaAlgorithm() {
        WebProperties webProperties = new WebProperties();
        webProperties.getCrypto().getAlgorithms().put("rsa", rsaAlgorithm());
        Cryptos.init(webProperties);

        String ciphertext = Cryptos.encrypt("hello", "rsa");

        assertEquals("hello", Cryptos.decrypt(ciphertext, "rsa"));
    }

    @Test
    void shouldEncryptAndDecryptWithConfiguredSm2Algorithm() {
        WebProperties webProperties = new WebProperties();
        webProperties.getCrypto().getAlgorithms().put("sm2", sm2Algorithm());
        Cryptos.init(webProperties);

        String ciphertext = Cryptos.encrypt("hello", "sm2");

        assertEquals("hello", Cryptos.decrypt(ciphertext, "sm2"));
    }

    private WebProperties.CryptoAlgorithm symmetricAlgorithm(CryptoAlgorithmType type) {
        WebProperties.CryptoAlgorithm algorithm = new WebProperties.CryptoAlgorithm();
        algorithm.setType(type);
        algorithm.setMode("ECB");
        algorithm.setPadding("PKCS5Padding");
        algorithm.setKey("MDEyMzQ1Njc4OWFiY2RlZg==");
        algorithm.setEncoding("BASE64");
        return algorithm;
    }

    private WebProperties.CryptoAlgorithm rsaAlgorithm() {
        WebProperties.CryptoAlgorithm algorithm = new WebProperties.CryptoAlgorithm();
        algorithm.setType(CryptoAlgorithmType.RSA);
        algorithm.setPublicKey("MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA1IadU55M1+Ly12xxmkHFhyOYS24aHuPtVvPcSEYBXDkDkOlEXo5qfYwmjzrmDA/aDa5D4JzvlUwltCyodWx99GQYypO/q72KFJjo1l7AyMYZlLXrq3fSD40bekOH0D40Ka9DKv+110vtmJDdc9kaLGMFlo4fyL8EcbLJgHNlRaRdryAVCWmXHdU11nQ0ZEwIQNZghO/7ZciyUiB982A2YpkXWtmwSnWPAQFK+TyzJ+jbw7JdpRQ65yiOm8cr87Ez2XEwMXqN3wMjaRagPeikFgvxIvkUwhkDd7LY6A22ulOngMYj+aXGy7Pum8zlrI6g9AS2Lii4AGJ033d+hSrFwwIDAQAB");
        algorithm.setPrivateKey("MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDUhp1TnkzX4vLXbHGaQcWHI5hLbhoe4+1W89xIRgFcOQOQ6URejmp9jCaPOuYMD9oNrkPgnO+VTCW0LKh1bH30ZBjKk7+rvYoUmOjWXsDIxhmUteurd9IPjRt6Q4fQPjQpr0Mq/7XXS+2YkN1z2RosYwWWjh/IvwRxssmAc2VFpF2vIBUJaZcd1TXWdDRkTAhA1mCE7/tlyLJSIH3zYDZimRda2bBKdY8BAUr5PLMn6NvDsl2lFDrnKI6bxyvzsTPZcTAxeo3fAyNpFqA96KQWC/Ei+RTCGQN3stjoDba6U6eAxiP5pcbLs+6bzOWsjqD0BLYuKLgAYnTfd36FKsXDAgMBAAECggEAAnqURZabfHEjNl86omGRNXBO2G8vH5xQKl5E++E4WgqemTp/StlyZVOuWqe4z/wA8rCzp5NtBlrEndAsVTvjHc44RwFnw0/X10GpUmBfwBFZC9XLXk0GRpqPIkmJwTE+8bWGA2+UGWMpuq8F39IKbulNj8hi/bgGPz7ZZtKLV3KVfR57H6UIJAOhl2KarHJ3WFyWWt6Zv71neKXt8vzhzbLDfAU096LKjDS4iCMHJvteluVzweK65V2BkyTHSUxc3bAnQ+QsJRRwh4AczBorcIdX24SGFmh6rBPZM6IipzfDECcOtnlq24/hT+WSJLgURmphf6W6ktls8kX7CLXSEQKBgQD2+wLqqA7+E2L2eNn562+iqmBdzwhciydvhYlUueEy2347/8CVpDuFmXIc119QgRL2djjy+J+kCvjpvCLC+EUZgv9DCqbH7Hh9eyQiDHcBJ6mjlhmqfXAfZFHGHKivaUBvm2NkoLgBwgb8Rc30Z+dZhzU6878PzH9mWcWCQhZ35wKBgQDcSX2rnGD3RGTZe4tWyajkK/v/8gBUhoSW6MoADydn8DVjEGJRbNaTc9xc3Y7arX4pIcn59098+UFHr9VuDARcoahls6zHRP/+xBICErX55/WqOymgvwtkffjTM9aqbiO8R0VjVr3mvGaXlbvQfAnxhi1qvdHODdeavx9DB7dXxQKBgGvvfPHSKmMtU8WFQTWwh6QeM2krzmqrQbvCLydA6MMSG5PbDG4ZTQ3w5mbmVLhZAFGHjxHYR+QoC2oSl1p/p6sjjazd3c9nb6zfAQaETgnKLJ/aPDM/CJ4feIFNCwdFxyw/S2uxtVjHU8gyNXTHZ9q/dSrjVS9xXof7WY64lRsnAoGBALfugQkGdIxhxhc1qd2YWfXSqAJITXBGNXVQAEJWZN/LWLhTmNRGHVBXQFsl/76leB7eAKg/kFZYROKXdY0Caa3yqQnqIUztVxahHiwu9VUqEhzGc3atrdSFvnqS4R3DAwGVPOG6Lqv3CW5ubrigqSjKkSd8QS9crXafOHoeej7FAoGALEwanU/RutZ5Vxo4DfjCUi9zwYcdPxXBRAZZjPPQQhwiD/lAYyHju2371gxvRjegTa6oIdvQFakWRGEck377mMevzvV37ZXEQbGH7mQqCh3qvsWK/leEy/8jvKgaaoyUq1Zr5PYLhHGN3J1JjY2AnKoV6ahcv9LPGJ402249r/8=");
        algorithm.setEncoding("BASE64");
        return algorithm;
    }

    private WebProperties.CryptoAlgorithm sm2Algorithm() {
        WebProperties.CryptoAlgorithm algorithm = new WebProperties.CryptoAlgorithm();
        algorithm.setType(CryptoAlgorithmType.SM2);
        algorithm.setPublicKey("MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEYVx9Xd3qRvVAKvaygV2upKy6eqCffWfMoyXuptbAGhIcLl7v9YUZcDkSgmW0/+I82CnjMJ9m1K36hEUoL+YWRA==");
        algorithm.setPrivateKey("MEECAQAwEwYHKoZIzj0CAQYIKoZIzj0DAQcEJzAlAgEBBCCwpoMXpvOprbQaOz1a1FYyQkSooZ6pGKT0tHya1UJqvA==");
        algorithm.setEncoding("BASE64");
        return algorithm;
    }
}
