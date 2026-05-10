package com.coderych.commons.satoken.autoconfigure;

import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SaTokenPropertiesTests {

    @Test
    void defaultValuesShouldBeCorrect() {
        SaTokenProperties properties = new SaTokenProperties();
        assertTrue(properties.isEnabled());
        assertArrayEquals(new String[]{"/**"}, properties.getInclude());
        assertArrayEquals(new String[]{}, properties.getExclude());
        assertArrayEquals(new String[]{}, properties.getSuperAdmins());
    }

    @Test
    void shouldBindCustomProperties() {
        Map<String, String> map = new HashMap<>();
        map.put("commons.sa-token.enabled", "false");
        map.put("commons.sa-token.include[0]", "/api/**");
        map.put("commons.sa-token.exclude[0]", "/api/public/**");
        map.put("commons.sa-token.exclude[1]", "/api/health");
        map.put("commons.sa-token.super-admins[0]", "admin");
        map.put("commons.sa-token.super-admins[1]", "root");

        MapConfigurationPropertySource source = new MapConfigurationPropertySource(map);
        Binder binder = new Binder(source);
        SaTokenProperties properties = binder.bind("commons.sa-token", Bindable.of(SaTokenProperties.class)).get();

        assertThat(properties.isEnabled()).isFalse();
        assertThat(properties.getInclude()).containsExactly("/api/**");
        assertThat(properties.getExclude()).containsExactly("/api/public/**", "/api/health");
        assertThat(properties.getSuperAdmins()).containsExactly("admin", "root");
    }

    @Test
    void shouldBindEnabledProperty() {
        Map<String, String> map = new HashMap<>();
        map.put("commons.sa-token.enabled", "false");

        MapConfigurationPropertySource source = new MapConfigurationPropertySource(map);
        Binder binder = new Binder(source);
        SaTokenProperties properties = binder.bind("commons.sa-token", Bindable.of(SaTokenProperties.class)).get();

        assertThat(properties.isEnabled()).isFalse();
    }

    @Test
    void shouldBindIncludeProperty() {
        Map<String, String> map = new HashMap<>();
        map.put("commons.sa-token.include[0]", "/web/**");
        map.put("commons.sa-token.include[1]", "/app/**");

        MapConfigurationPropertySource source = new MapConfigurationPropertySource(map);
        Binder binder = new Binder(source);
        SaTokenProperties properties = binder.bind("commons.sa-token", Bindable.of(SaTokenProperties.class)).get();

        assertThat(properties.getInclude()).containsExactly("/web/**", "/app/**");
    }

    @Test
    void shouldBindExcludeProperty() {
        Map<String, String> map = new HashMap<>();
        map.put("commons.sa-token.exclude[0]", "/public/**");

        MapConfigurationPropertySource source = new MapConfigurationPropertySource(map);
        Binder binder = new Binder(source);
        SaTokenProperties properties = binder.bind("commons.sa-token", Bindable.of(SaTokenProperties.class)).get();

        assertThat(properties.getExclude()).containsExactly("/public/**");
    }

    @Test
    void shouldBindSuperAdminsProperty() {
        Map<String, String> map = new HashMap<>();
        map.put("commons.sa-token.super-admins[0]", "superuser");

        MapConfigurationPropertySource source = new MapConfigurationPropertySource(map);
        Binder binder = new Binder(source);
        SaTokenProperties properties = binder.bind("commons.sa-token", Bindable.of(SaTokenProperties.class)).get();

        assertThat(properties.getSuperAdmins()).containsExactly("superuser");
    }
}
