package com.coderych.commons.satoken.core;

import cn.dev33.satoken.stp.StpUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mockStatic;

class LoginUserTests {

    @BeforeAll
    static void setUp() throws Exception {
        Field field = LoginUser.class.getDeclaredField("SUPER_ADMINS");
        field.setAccessible(true);
        field.set(null, Arrays.asList("admin", "root"));
    }

    @Test
    void isSuperAdminWhenUserIsSuperAdminShouldReturnTrue() {
        assertTrue(LoginUser.isSuperAdmin("admin"));
        assertTrue(LoginUser.isSuperAdmin("root"));
    }

    @Test
    void isSuperAdminWhenUserIsNotSuperAdminShouldReturnFalse() {
        assertFalse(LoginUser.isSuperAdmin("user"));
        assertFalse(LoginUser.isSuperAdmin("guest"));
    }

    @Test
    void isSuperAdminWhenUsernameIsNullShouldReturnFalse() {
        assertFalse(LoginUser.isSuperAdmin(null));
    }

    @Test
    void isSuperAdminWhenUsernameIsEmptyShouldReturnFalse() {
        assertFalse(LoginUser.isSuperAdmin(""));
    }

    @Test
    void isSuperAdminWhenSuperAdminsIsNullShouldReturnFalse() throws Exception {
        Field field = LoginUser.class.getDeclaredField("SUPER_ADMINS");
        field.setAccessible(true);
        Object original = field.get(null);
        try {
            field.set(null, null);
            assertFalse(LoginUser.isSuperAdmin("admin"));
        } finally {
            field.set(null, original);
        }
    }

    @Test
    void isSuperAdminWhenSuperAdminsIsEmptyShouldReturnFalse() throws Exception {
        Field field = LoginUser.class.getDeclaredField("SUPER_ADMINS");
        field.setAccessible(true);
        Object original = field.get(null);
        try {
            field.set(null, List.of());
            assertFalse(LoginUser.isSuperAdmin("admin"));
        } finally {
            field.set(null, original);
        }
    }

    @Test
    void getLoginUserIdWhenLoggedInShouldReturnId() {
        try (MockedStatic<StpUtil> mocked = mockStatic(StpUtil.class)) {
            mocked.when(StpUtil::getLoginIdAsString).thenReturn("12345");
            assertEquals("12345", LoginUser.getLoginUserId());
        }
    }

    @Test
    void getLoginUserIdOrDefaultWhenLoggedInShouldReturnId() {
        try (MockedStatic<StpUtil> mocked = mockStatic(StpUtil.class)) {
            mocked.when(StpUtil::getLoginIdAsString).thenReturn("12345");
            assertEquals("12345", LoginUser.getLoginUserIdOrDefault("default"));
        }
    }

    @Test
    void getLoginUserIdOrDefaultWhenNotLoggedInShouldReturnDefault() {
        try (MockedStatic<StpUtil> mocked = mockStatic(StpUtil.class)) {
            mocked.when(StpUtil::getLoginIdAsString).thenThrow(new RuntimeException("Not logged in"));
            assertEquals("default", LoginUser.getLoginUserIdOrDefault("default"));
        }
    }
}
