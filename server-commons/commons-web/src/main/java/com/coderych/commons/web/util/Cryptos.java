package com.coderych.commons.web.util;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.HexUtil;
import cn.hutool.crypto.asymmetric.AsymmetricCrypto;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.SM2;
import cn.hutool.crypto.symmetric.SymmetricCrypto;
import com.coderych.commons.core.util.STR;
import com.coderych.commons.web.autoconfigure.WebProperties;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.nio.charset.StandardCharsets;

/**
 * 加解密工具类，支持 AES、SM4、RSA、SM2 四种算法。
 * <p>通过 {@link #init(WebProperties)} 加载配置，使用算法名称（配置中的 key）作为加解密入口。</p>
 *
 * @author YCH
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Cryptos {

    @Getter
    private static volatile WebProperties.Crypto cryptoProperties = new WebProperties.Crypto();

    public static synchronized void init(WebProperties webProperties) {
        if (webProperties == null) {
            throw new IllegalArgumentException("WebProperties must not be null");
        }
        Cryptos.cryptoProperties = webProperties.getCrypto();
    }

    public static String encrypt(String plaintext, String key) {
        WebProperties.CryptoAlgorithm properties = getAlgorithmProperties(key);
        return switch (properties.getType()) {
            case AES -> encryptBySymmetric(plaintext, properties, "AES");
            case SM4 -> encryptBySymmetric(plaintext, properties, "SM4");
            case RSA -> encryptByRsa(plaintext, properties);
            case SM2 -> encryptBySm2(plaintext, properties);
        };
    }

    public static String decrypt(String ciphertext, String key) {
        WebProperties.CryptoAlgorithm properties = getAlgorithmProperties(key);
        return switch (properties.getType()) {
            case AES -> decryptBySymmetric(ciphertext, properties, "AES");
            case SM4 -> decryptBySymmetric(ciphertext, properties, "SM4");
            case RSA -> decryptByRsa(ciphertext, properties);
            case SM2 -> decryptBySm2(ciphertext, properties);
        };
    }

    private static WebProperties.CryptoAlgorithm getAlgorithmProperties(String key) {
        WebProperties.CryptoAlgorithm algorithmProperties = cryptoProperties.getAlgorithms().get(key);
        if (algorithmProperties == null) {
            throw new IllegalStateException("未找到加密算法配置: " + key);
        }
        if (algorithmProperties.getType() == null) {
            throw new IllegalStateException("未配置加密算法类型: " + key);
        }
        return algorithmProperties;
    }

    private static SymmetricCrypto createSymmetricCrypto(WebProperties.CryptoAlgorithm properties, String algorithm) {
        byte[] keyBytes = decode(properties.getKey(), properties.getEncoding());
        byte[] ivBytes = properties.getIv() != null ? decode(properties.getIv(), properties.getEncoding()) : null;
        javax.crypto.SecretKey secretKey = new javax.crypto.spec.SecretKeySpec(keyBytes, algorithm);
        javax.crypto.spec.IvParameterSpec ivSpec = ivBytes != null ? new javax.crypto.spec.IvParameterSpec(ivBytes) : null;
        String fullAlgorithm = buildSymmetricAlgorithm(algorithm, properties);
        return ivSpec != null
                ? new SymmetricCrypto(fullAlgorithm, secretKey, ivSpec)
                : new SymmetricCrypto(fullAlgorithm, secretKey);
    }

    private static String encryptBySymmetric(String plaintext, WebProperties.CryptoAlgorithm properties, String algorithm) {
        SymmetricCrypto crypto = createSymmetricCrypto(properties, algorithm);
        byte[] encrypted = crypto.encrypt(plaintext.getBytes(StandardCharsets.UTF_8));
        return encode(encrypted, properties.getEncoding());
    }

    private static String decryptBySymmetric(String ciphertext, WebProperties.CryptoAlgorithm properties, String algorithm) {
        SymmetricCrypto crypto = createSymmetricCrypto(properties, algorithm);
        return new String(crypto.decrypt(decode(ciphertext, properties.getEncoding())), StandardCharsets.UTF_8);
    }

    private static AsymmetricCrypto createRsaCrypto(WebProperties.CryptoAlgorithm properties) {
        return new AsymmetricCrypto("RSA",
                decode(properties.getPrivateKey(), properties.getEncoding()),
                decode(properties.getPublicKey(), properties.getEncoding()));
    }

    private static String encryptByRsa(String plaintext, WebProperties.CryptoAlgorithm properties) {
        AsymmetricCrypto crypto = createRsaCrypto(properties);
        byte[] encrypted = crypto.encrypt(plaintext.getBytes(StandardCharsets.UTF_8), KeyType.PublicKey);
        return encode(encrypted, properties.getEncoding());
    }

    private static String decryptByRsa(String ciphertext, WebProperties.CryptoAlgorithm properties) {
        AsymmetricCrypto crypto = createRsaCrypto(properties);
        return new String(crypto.decrypt(decode(ciphertext, properties.getEncoding()), KeyType.PrivateKey),
                StandardCharsets.UTF_8);
    }

    private static SM2 createSm2Crypto(WebProperties.CryptoAlgorithm properties) {
        return new SM2(decode(properties.getPrivateKey(), properties.getEncoding()),
                decode(properties.getPublicKey(), properties.getEncoding()));
    }

    private static String encryptBySm2(String plaintext, WebProperties.CryptoAlgorithm properties) {
        SM2 sm2 = createSm2Crypto(properties);
        byte[] encrypted = sm2.encrypt(plaintext.getBytes(StandardCharsets.UTF_8));
        return encode(encrypted, properties.getEncoding());
    }

    private static String decryptBySm2(String ciphertext, WebProperties.CryptoAlgorithm properties) {
        SM2 sm2 = createSm2Crypto(properties);
        return new String(sm2.decrypt(decode(ciphertext, properties.getEncoding())), StandardCharsets.UTF_8);
    }

    private static String buildSymmetricAlgorithm(String algorithm, WebProperties.CryptoAlgorithm properties) {
        if (STR.isNotBlank(properties.getMode()) && STR.isNotBlank(properties.getPadding())) {
            return algorithm + "/" + properties.getMode() + "/" + properties.getPadding();
        }
        return algorithm;
    }

    private static String encode(byte[] data, String encoding) {
        return "HEX".equalsIgnoreCase(encoding) ? HexUtil.encodeHexStr(data) : Base64.encode(data);
    }

    private static byte[] decode(String data, String encoding) {
        return "HEX".equalsIgnoreCase(encoding) ? HexUtil.decodeHex(data) : Base64.decode(data);
    }
}
