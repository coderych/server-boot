package com.coderych.commons.satoken.core;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.stp.StpUtil;
import com.coderych.commons.core.enums.ResultCode;
import com.coderych.commons.core.exception.BizException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 登录用户工具类，封装 Sa-Token 的会话与权限操作。
 * <p>通过 {@link #init(String, String[])} 初始化缓存键前缀和超级管理员列表，所有权限/角色检查方法均会自动放行超级管理员。</p>
 *
 * @author YCH
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class LoginUser {
    private static final AtomicBoolean INITIALIZED = new AtomicBoolean(false);
    /**
     * Sa-Token 令牌名称。
     */
    public static volatile String TOKEN_NAME;
    /**
     * 登录用户名缓存键前缀。
     */
    public static volatile String LOGIN_USERNAME_CACHE_KEY;
    /**
     * 登录租户缓存键前缀。
     */
    public static volatile String LOGIN_TENANT_CACHE_KEY;
    /**
     * 登录用户缓存键前缀。
     */
    public static volatile String LOGIN_USER_CACHE_KEY;
    /**
     * 用户角色缓存键前缀。
     */
    public static volatile String LOGIN_USER_FIND_ROLE_CACHE_KEY;
    /**
     * 角色权限缓存键前缀。
     */
    public static volatile String LOGIN_ROLE_FIND_PERMISSION_CACHE_KEY;
    /**
     * 超级管理员列表。
     */
    public static volatile List<String> SUPER_ADMINS;

    /**
     * 初始化登录用户模块，使用 CAS 保证仅执行一次。
     *
     * @param tokenName   Sa-Token 令牌名称，用于构建缓存键前缀
     * @param superAdmins 超级管理员用户名数组，拥有所有权限
     */
    public static void init(String tokenName, String[] superAdmins) {
        if (!INITIALIZED.compareAndSet(false, true)) {
            return;
        }
        TOKEN_NAME = tokenName;
        LOGIN_USERNAME_CACHE_KEY = TOKEN_NAME + ":login:username:";
        LOGIN_USER_CACHE_KEY = TOKEN_NAME + ":login:user:";
        LOGIN_TENANT_CACHE_KEY = TOKEN_NAME + ":login:tenant:";
        LOGIN_USER_FIND_ROLE_CACHE_KEY = TOKEN_NAME + ":login:user:role:";
        LOGIN_ROLE_FIND_PERMISSION_CACHE_KEY = TOKEN_NAME + ":login:role:permission:";
        SUPER_ADMINS = Arrays.asList(superAdmins);
    }

    public static String getLoginUserId() {
        return StpUtil.getLoginIdAsString();
    }

    public static String getLoginUserIdOrDefault(String defaultValue) {
        try {
            return getLoginUserId();
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static String getLoginUsername() {
        String username = SaManager.getSaTokenDao().get(LOGIN_USERNAME_CACHE_KEY + getLoginUserId());
        if (username == null) {
            throw new BizException(ResultCode.UNAUTHORIZED);
        }
        return username;
    }

    public static String getLoginUsernameOrDefault(String defaultValue) {
        try {
            return getLoginUsername();
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static String getLoginTenantId() {
        return SaManager.getSaTokenDao().get(LOGIN_TENANT_CACHE_KEY + getLoginUserId());
    }

    public static String getLoginTenantIdOrDefault(String defaultValue) {
        try {
            String tenantId = getLoginTenantId();
            return tenantId == null ? defaultValue : tenantId;
        } catch (Exception exception) {
            return defaultValue;
        }
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> profile() {
        return (Map<String, Object>) SaManager.getSaTokenDao().getObject(LOGIN_USER_CACHE_KEY + getLoginUserId());
    }

    public static boolean isLogin() {
        return StpUtil.isLogin();
    }

    public static boolean hasPermission(String permission) {
        return isSuperAdmin(getLoginUsername()) || StpUtil.hasPermission(permission);
    }

    public static boolean hasPermissionAnd(String... permissions) {
        return isSuperAdmin(getLoginUsername()) || StpUtil.hasPermissionAnd(permissions);
    }

    public static boolean hasPermissionOr(String... permissions) {
        return isSuperAdmin(getLoginUsername()) || StpUtil.hasPermissionOr(permissions);
    }

    public static void checkPermission(String permission) {
        if (!isSuperAdmin(getLoginUsername())) {
            StpUtil.checkPermission(permission);
        }
    }

    public static void checkPermissionAnd(String... permissions) {
        if (!isSuperAdmin(getLoginUsername())) {
            StpUtil.checkPermissionAnd(permissions);
        }
    }

    public static void checkPermissionOr(String... permissions) {
        if (!isSuperAdmin(getLoginUsername())) {
            StpUtil.checkPermissionOr(permissions);
        }
    }

    public static boolean hasRole(String role) {
        return isSuperAdmin(getLoginUsername()) || StpUtil.hasRole(role);
    }

    public static boolean hasRoleAnd(String... roles) {
        return isSuperAdmin(getLoginUsername()) || StpUtil.hasRoleAnd(roles);
    }

    public static boolean hasRoleOr(String... roles) {
        return isSuperAdmin(getLoginUsername()) || StpUtil.hasRoleOr(roles);
    }

    public static void checkRole(String role) {
        if (!isSuperAdmin(getLoginUsername())) {
            StpUtil.checkRole(role);
        }
    }

    public static void checkRoleAnd(String... roles) {
        if (!isSuperAdmin(getLoginUsername())) {
            StpUtil.checkRoleAnd(roles);
        }
    }

    public static void checkRoleOr(String... roles) {
        if (!isSuperAdmin(getLoginUsername())) {
            StpUtil.checkRoleOr(roles);
        }
    }

    public static boolean isSuperAdmin() {
        return isSuperAdmin(getLoginUsername());
    }

    public static boolean isSuperAdmin(String username) {
        return SUPER_ADMINS != null && SUPER_ADMINS.contains(username);
    }
}
