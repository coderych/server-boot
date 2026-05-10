package com.coderych.commons.core.util.web;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WebUtilsTests {

    @AfterEach
    void tearDown() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void shouldResolveIpAndUserAgent() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-Forwarded-For", "1.1.1.1, 2.2.2.2");
        request.addHeader("User-Agent", "JUnit");
        request.setMethod("GET");
        request.setRequestURI("/demo");
        request.setRemoteAddr("127.0.0.1");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        assertEquals("1.1.1.1", WebUtils.getIpAddress());
        assertEquals("JUnit", WebUtils.getUserAgent());
        assertEquals("/demo", WebUtils.getRequestUri());
        assertEquals("GET", WebUtils.getMethod());
    }
}
