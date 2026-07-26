package com.coderych.commons.core.util;

import cn.hutool.crypto.digest.BCrypt;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 密码工具类，提供基于用户独立盐值的 BCrypt 哈希与校验。
 *
 * @author YCH
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PasswordUtils {
    /**
     * 生成随机盐值。
     *
     * @return 32 位 UUID 盐值
     */
    public static String generateSalt() {
        return IdWorker.fastSimpleUUID();
    }

    /**
     * 对明文密码加盐后进行 BCrypt 哈希。
     *
     * @param rawPassword 明文密码
     * @param salt        用户盐值
     * @return BCrypt 哈希值
     */
    public static String hash(String rawPassword, String salt) {
        return BCrypt.hashpw(salt + rawPassword, BCrypt.gensalt());
    }

    /**
     * 校验明文密码与哈希值是否匹配。
     *
     * @param rawPassword    明文密码
     * @param salt           用户盐值
     * @param hashedPassword BCrypt 哈希值
     * @return 是否匹配
     */
    public static boolean matches(String rawPassword, String salt, String hashedPassword) {
        return BCrypt.checkpw(salt + rawPassword, hashedPassword);
    }
}
