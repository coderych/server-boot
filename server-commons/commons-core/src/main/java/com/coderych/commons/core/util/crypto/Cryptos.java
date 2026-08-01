package com.coderych.commons.core.util.crypto;

import cn.hutool.core.codec.Base64;
import cn.hutool.crypto.asymmetric.AsymmetricCrypto;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.SM2;
import cn.hutool.crypto.symmetric.SymmetricCrypto;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.nio.charset.StandardCharsets;

/**
 * 加解密工具类，支持 AES、SM4、RSA、SM2 四种常用算法（对称：AES/SM4，非对称：RSA/SM2）。
 * <p>密钥格式约定：</p>
 * <ul>
 *     <li>对称（AES/SM4）：{@code key} 为明文字符串，按 UTF-8 取字节（AES 需 16/24/32 字节，SM4 需 16 字节），
 *     ECB/PKCS5 模式，无需 IV；</li>
 *     <li>非对称（RSA/SM2）：加密传 Base64 编码的公钥（X509 规范），解密传 Base64 编码的私钥（PKCS#8 规范）。</li>
 * </ul>
 * <p>密文统一为 Base64 编码。</p>
 *
 * @author YCH
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Cryptos {

    /**
     * 加密明文。
     *
     * @param plaintext 明文
     * @param type      算法类型
     * @param key       对称密钥或非对称公钥
     * @return Base64 密文
     */
    public static String encrypt(String plaintext, CryptoType type, String key) {
        return switch (type) {
            case AES -> encryptBySymmetric(plaintext, key, "AES");
            case SM4 -> encryptBySymmetric(plaintext, key, "SM4");
            case RSA -> encryptByRsa(plaintext, key);
            case SM2 -> encryptBySm2(plaintext, key);
        };
    }

    /**
     * 解密密文。
     *
     * @param ciphertext Base64 密文
     * @param type       算法类型
     * @param key        对称密钥或非对称私钥
     * @return 明文
     */
    public static String decrypt(String ciphertext, CryptoType type, String key) {
        return switch (type) {
            case AES -> decryptBySymmetric(ciphertext, key, "AES");
            case SM4 -> decryptBySymmetric(ciphertext, key, "SM4");
            case RSA -> decryptByRsa(ciphertext, key);
            case SM2 -> decryptBySm2(ciphertext, key);
        };
    }

    private static String encryptBySymmetric(String plaintext, String key, String name) {
        SymmetricCrypto crypto = new SymmetricCrypto(name + "/ECB/PKCS5Padding", key.getBytes(StandardCharsets.UTF_8));
        return Base64.encode(crypto.encrypt(plaintext.getBytes(StandardCharsets.UTF_8)));
    }

    private static String decryptBySymmetric(String ciphertext, String key, String name) {
        SymmetricCrypto crypto = new SymmetricCrypto(name + "/ECB/PKCS5Padding", key.getBytes(StandardCharsets.UTF_8));
        return new String(crypto.decrypt(Base64.decode(ciphertext)), StandardCharsets.UTF_8);
    }

    private static String encryptByRsa(String plaintext, String publicKey) {
        AsymmetricCrypto crypto = new AsymmetricCrypto("RSA", null, Base64.decode(publicKey));
        return Base64.encode(crypto.encrypt(plaintext.getBytes(StandardCharsets.UTF_8), KeyType.PublicKey));
    }

    private static String decryptByRsa(String ciphertext, String privateKey) {
        AsymmetricCrypto crypto = new AsymmetricCrypto("RSA", Base64.decode(privateKey), null);
        return new String(crypto.decrypt(Base64.decode(ciphertext), KeyType.PrivateKey), StandardCharsets.UTF_8);
    }

    private static String encryptBySm2(String plaintext, String publicKey) {
        SM2 sm2 = new SM2(null, publicKey);
        return Base64.encode(sm2.encrypt(plaintext.getBytes(StandardCharsets.UTF_8)));
    }

    private static String decryptBySm2(String ciphertext, String privateKey) {
        SM2 sm2 = new SM2(privateKey, null);
        return new String(sm2.decrypt(Base64.decode(ciphertext)), StandardCharsets.UTF_8);
    }
}
