package com.coderych.commons.core.util.crypto;

import com.coderych.commons.core.util.crypto.CryptoType;
import com.coderych.commons.core.util.crypto.Cryptos;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CryptosTests {

    private static final String AES_KEY = "0123456789abcdef";

    @Test
    void shouldEncryptAndDecryptWithAesAlgorithm() {
        String ciphertext = Cryptos.encrypt("hello", CryptoType.AES, AES_KEY);

        assertEquals("hello", Cryptos.decrypt(ciphertext, CryptoType.AES, AES_KEY));
    }

    @Test
    void shouldEncryptAndDecryptWithSm4Algorithm() {
        String ciphertext = Cryptos.encrypt("hello", CryptoType.SM4, AES_KEY);

        assertEquals("hello", Cryptos.decrypt(ciphertext, CryptoType.SM4, AES_KEY));
    }

    @Test
    void shouldEncryptAndDecryptWithRsaAlgorithm() {
        String ciphertext = Cryptos.encrypt("hello", CryptoType.RSA, RSA_PUBLIC_KEY);

        assertEquals("hello", Cryptos.decrypt(ciphertext, CryptoType.RSA, RSA_PRIVATE_KEY));
    }

    @Test
    void shouldEncryptAndDecryptWithSm2Algorithm() {
        String ciphertext = Cryptos.encrypt("hello", CryptoType.SM2, SM2_PUBLIC_KEY);

        assertEquals("hello", Cryptos.decrypt(ciphertext, CryptoType.SM2, SM2_PRIVATE_KEY));
    }

    private static final String RSA_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA1IadU55M1+Ly12xxmkHFhyOYS24aHuPtVvPcSEYBXDkDkOlEXo5qfYwmjzrmDA/aDa5D4JzvlUwltCyodWx99GQYypO/q72KFJjo1l7AyMYZlLXrq3fSD40bekOH0D40Ka9DKv+110vtmJDdc9kaLGMFlo4fyL8EcbLJgHNlRaRdryAVCWmXHdU11nQ0ZEwIQNZghO/7ZciyUiB982A2YpkXWtmwSnWPAQFK+TyzJ+jbw7JdpRQ65yiOm8cr87Ez2XEwMXqN3wMjaRagPeikFgvxIvkUwhkDd7LY6A22ulOngMYj+aXGy7Pum8zlrI6g9AS2Lii4AGJ033d+hSrFwwIDAQAB";
    private static final String RSA_PRIVATE_KEY = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDUhp1TnkzX4vLXbHGaQcWHI5hLbhoe4+1W89xIRgFcOQOQ6URejmp9jCaPOuYMD9oNrkPgnO+VTCW0LKh1bH30ZBjKk7+rvYoUmOjWXsDIxhmUteurd9IPjRt6Q4fQPjQpr0Mq/7XXS+2YkN1z2RosYwWWjh/IvwRxssmAc2VFpF2vIBUJaZcd1TXWdDRkTAhA1mCE7/tlyLJSIH3zYDZimRda2bBKdY8BAUr5PLMn6NvDsl2lFDrnKI6bxyvzsTPZcTAxeo3fAyNpFqA96KQWC/Ei+RTCGQN3stjoDba6U6eAxiP5pcbLs+6bzOWsjqD0BLYuKLgAYnTfd36FKsXDAgMBAAECggEAAnqURZabfHEjNl86omGRNXBO2G8vH5xQKl5E++E4WgqemTp/StlyZVOuWqe4z/wA8rCzp5NtBlrEndAsVTvjHc44RwFnw0/X10GpUmBfwBFZC9XLXk0GRpqPIkmJwTE+8bWGA2+UGWMpuq8F39IKbulNj8hi/bgGPz7ZZtKLV3KVfR57H6UIJAOhl2KarHJ3WFyWWt6Zv71neKXt8vzhzbLDfAU096LKjDS4iCMHJvteluVzweK65V2BkyTHSUxc3bAnQ+QsJRRwh4AczBorcIdX24SGFmh6rBPZM6IipzfDECcOtnlq24/hT+WSJLgURmphf6W6ktls8kX7CLXSEQKBgQD2+wLqqA7+E2L2eNn562+iqmBdzwhciydvhYlUueEy2347/8CVpDuFmXIc119QgRL2djjy+J+kCvjpvCLC+EUZgv9DCqbH7Hh9eyQiDHcBJ6mjlhmqfXAfZFHGHKivaUBvm2NkoLgBwgb8Rc30Z+dZhzU6878PzH9mWcWCQhZ35wKBgQDcSX2rnGD3RGTZe4tWyajkK/v/8gBUhoSW6MoADydn8DVjEGJRbNaTc9xc3Y7arX4pIcn59098+UFHr9VuDARcoahls6zHRP/+xBICErX55/WqOymgvwtkffjTM9aqbiO8R0VjVr3mvGaXlbvQfAnxhi1qvdHODdeavx9DB7dXxQKBgGvvfPHSKmMtU8WFQTWwh6QeM2krzmqrQbvCLydA6MMSG5PbDG4ZTQ3w5mbmVLhZAFGHjxHYR+QoC2oSl1p/p6sjjazd3c9nb6zfAQaETgnKLJ/aPDM/CJ4feIFNCwdFxyw/S2uxtVjHU8gyNXTHZ9q/dSrjVS9xXof7WY64lRsnAoGBALfugQkGdIxhxhc1qd2YWfXSqAJITXBGNXVQAEJWZN/LWLhTmNRGHVBXQFsl/76leB7eAKg/kFZYROKXdY0Caa3yqQnqIUztVxahHiwu9VUqEhzGc3atrdSFvnqS4R3DAwGVPOG6Lqv3CW5ubrigqSjKkSd8QS9crXafOHoeej7FAoGALEwanU/RutZ5Vxo4DfjCUi9zwYcdPxXBRAZZjPPQQhwiD/lAYyHju2371gxvRjegTa6oIdvQFakWRGEck377mMevzvV37ZXEQbGH7mQqCh3qvsWK/leEy/8jvKgaaoyUq1Zr5PYLhHGN3J1JjY2AnKoV6ahcv9LPGJ402249r/8=";

    private static final String SM2_PUBLIC_KEY = "MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEYVx9Xd3qRvVAKvaygV2upKy6eqCffWfMoyXuptbAGhIcLl7v9YUZcDkSgmW0/+I82CnjMJ9m1K36hEUoL+YWRA==";
    private static final String SM2_PRIVATE_KEY = "MEECAQAwEwYHKoZIzj0CAQYIKoZIzj0DAQcEJzAlAgEBBCCwpoMXpvOprbQaOz1a1FYyQkSooZ6pGKT0tHya1UJqvA==";
}
