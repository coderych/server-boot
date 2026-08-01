package com.coderych.commons.web.crypto;

/**
 * 接口加解密服务 SPI，由业务模块实现。
 * <p>加解密算法实现、密钥与开关均由业务端自行决定（可直接复用 commons-core 的 {@code Cryptos} 工具类），
 * 不再依赖 {@code commons.web.crypto.*} 配置：业务实现可从 config 表实时读取，实现动态开关与热切换。</p>
 * <p>加密范围由业务端通过 {@link #globalEncrypt()} / {@link #globalDecrypt()} 决定，两个开关相互独立：</p>
 * <ul>
 *     <li>均返回 {@code false}（默认）：仅对标注 {@code @Crypto} 的接口加解密；</li>
 *     <li>{@link #globalEncrypt()} 返回 {@code true}：全部接口响应加密（可只开这一项，请求保持明文）；</li>
 *     <li>{@link #globalDecrypt()} 返回 {@code true}：全部接口请求解密。</li>
 * </ul>
 * <p>两种模式下 {@code @Crypto} 注解均优先：标注 {@code encrypt=false} / {@code decrypt=false} 的接口
 * 会从对应的全局开关中排除，标注了 {@code algorithm} 的接口使用注解指定的算法。</p>
 * <p>用法：在 Controller 类或方法上标注 {@code @Crypto(algorithm = "xxx")}，
 * {@code algorithm} 即本接口方法中的算法名参数，由业务实现解析为具体密钥等参数。</p>
 *
 * @author YCH
 */
public interface CryptoService {

    /**
     * 是否全局响应加密：返回 {@code true} 时所有接口的响应体加密（无注解接口使用 {@link #defaultAlgorithm()}），
     * 与 {@link #globalDecrypt()} 相互独立，可只开启其中一项。
     * 在每次请求时调用，业务端从 config 表读取即可动态切换。
     *
     * @return 是否全局响应加密
     */
    default boolean globalEncrypt() {
        return false;
    }

    /**
     * 是否全局请求解密：返回 {@code true} 时所有接口的请求体解密（无注解接口使用 {@link #defaultAlgorithm()}），
     * 与 {@link #globalEncrypt()} 相互独立，可只开启其中一项。
     * 在每次请求时调用，业务端从 config 表读取即可动态切换。
     *
     * @return 是否全局请求解密
     */
    default boolean globalDecrypt() {
        return false;
    }

    /**
     * 全局加密/解密模式下无 {@code @Crypto} 注解接口使用的默认算法名，仅在 {@link #globalEncrypt()} 或
     * {@link #globalDecrypt()} 返回 {@code true} 时需要实现。
     *
     * @return 默认算法名
     */
    default String defaultAlgorithm() {
        throw new UnsupportedOperationException("全局加密/解密模式下必须实现 defaultAlgorithm()");
    }

    /**
     * 指定算法当前是否启用，在每次请求时调用，业务端从 config 表读取即可实现动态开关（改表立即生效，无需重启）。
     * 返回 {@code false} 时，该算法对应的接口按明文透传（请求不解密、响应不加密）。
     *
     * @param algorithm 算法名（与 {@code @Crypto} 注解的 {@code algorithm} 一致）
     * @return 是否启用
     */
    boolean enabled(String algorithm);

    /**
     * 加密明文。
     *
     * @param plaintext 明文
     * @param algorithm 算法名
     * @return 密文
     */
    String encrypt(String plaintext, String algorithm);

    /**
     * 解密密文。
     *
     * @param ciphertext 密文
     * @param algorithm  算法名
     * @return 明文
     */
    String decrypt(String ciphertext, String algorithm);

    /**
     * 加密响应的默认 Content-Type，业务端可覆盖。
     *
     * @return 响应 Content-Type
     */
    default String defaultContentType() {
        return "text/plain;charset=UTF-8";
    }
}
