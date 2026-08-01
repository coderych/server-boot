package com.coderych.commons.web.autoconfigure;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WebPropertiesTests {

    @Test
    void shouldHaveCorrectDefaultValues() {
        WebProperties properties = new WebProperties();

        assertTrue(properties.isEnabled());

        assertTrue(properties.getException().isEnabled());

        assertTrue(properties.getXss().isEnabled());
        assertEquals("clean", properties.getXss().getMode());
        assertNotNull(properties.getXss().getExcludePaths());
        assertTrue(properties.getXss().getExcludePaths().isEmpty());

        assertTrue(properties.getCors().isEnabled());
        assertEquals("/**", properties.getCors().getPathPattern());
        assertNotNull(properties.getCors().getAllowedOrigins());
        assertNotNull(properties.getCors().getAllowedOriginPatterns());
        assertNotNull(properties.getCors().getAllowedMethods());
        assertNotNull(properties.getCors().getAllowedHeaders());
        assertNotNull(properties.getCors().getExposedHeaders());
        assertNull(properties.getCors().getAllowCredentials());
        assertNull(properties.getCors().getMaxAge());
    }

    @Test
    void shouldSetEnabledFlag() {
        WebProperties properties = new WebProperties();
        properties.setEnabled(false);
        assertFalse(properties.isEnabled());
    }

    @Test
    void shouldSetExceptionProperties() {
        WebProperties properties = new WebProperties();
        properties.getException().setEnabled(false);
        assertFalse(properties.getException().isEnabled());
    }

    @Test
    void shouldSetXssProperties() {
        WebProperties properties = new WebProperties();
        properties.getXss().setEnabled(false);
        properties.getXss().setMode("escape");
        properties.getXss().setExcludePaths(List.of("/api/public", "/health"));

        assertFalse(properties.getXss().isEnabled());
        assertEquals("escape", properties.getXss().getMode());
        assertEquals(2, properties.getXss().getExcludePaths().size());
        assertEquals("/api/public", properties.getXss().getExcludePaths().get(0));
    }

    @Test
    void shouldSetCorsProperties() {
        WebProperties properties = new WebProperties();
        properties.getCors().setEnabled(false);
        properties.getCors().setPathPattern("/api/**");
        properties.getCors().setAllowedOrigins(List.of("http://localhost:3000"));
        properties.getCors().setAllowedOriginPatterns(List.of("http://*.example.com"));
        properties.getCors().setAllowedMethods(List.of("GET", "POST"));
        properties.getCors().setAllowedHeaders(List.of("Authorization", "Content-Type"));
        properties.getCors().setExposedHeaders(List.of("X-Custom-Header"));
        properties.getCors().setAllowCredentials(true);
        properties.getCors().setMaxAge(3600L);

        assertFalse(properties.getCors().isEnabled());
        assertEquals("/api/**", properties.getCors().getPathPattern());
        assertEquals(1, properties.getCors().getAllowedOrigins().size());
        assertEquals(1, properties.getCors().getAllowedOriginPatterns().size());
        assertEquals(2, properties.getCors().getAllowedMethods().size());
        assertEquals(2, properties.getCors().getAllowedHeaders().size());
        assertEquals(1, properties.getCors().getExposedHeaders().size());
        assertTrue(properties.getCors().getAllowCredentials());
        assertEquals(3600L, properties.getCors().getMaxAge());
    }
}
