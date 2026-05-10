package com.coderych.commons.web.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class XssCleanerTests {

    @Test
    void shouldReturnNullForNullInput() {
        assertNull(XssCleaner.clean(null));
    }

    @Test
    void shouldReturnEmptyForEmptyInput() {
        assertEquals("", XssCleaner.clean(""));
    }

    @Test
    void shouldRemoveScriptTags() {
        String result = XssCleaner.clean("<script>alert('xss')</script>");
        assertTrue(result.isEmpty() || !result.contains("<script>"));
    }

    @Test
    void shouldRemoveImgOnError() {
        String result = XssCleaner.clean("<img onerror=\"alert(1)\" src=\"x\">");
        assertTrue(result.isEmpty() || !result.contains("onerror"));
    }

    @Test
    void shouldRemoveJavascriptHref() {
        String result = XssCleaner.clean("<a href=\"javascript:alert(1)\">click</a>");
        assertTrue(result.isEmpty() || !result.contains("javascript:"));
    }

    @Test
    void shouldPreserveNormalText() {
        String input = "Hello World 123";
        assertEquals("Hello World 123", XssCleaner.clean(input));
    }

    @Test
    void shouldRemoveEventHandlers() {
        String result = XssCleaner.clean("<div onclick=\"alert(1)\">content</div>");
        assertTrue(result.isEmpty() || !result.contains("onclick"));
    }

    @Test
    void shouldRemoveStyleExpression() {
        String result = XssCleaner.clean("<div style=\"background:url(javascript:alert(1))\">content</div>");
        assertTrue(result.isEmpty() || !result.contains("javascript:"));
    }

    @Test
    void shouldHandlePlainTextWithSpecialCharacters() {
        String input = "Price: 100 < 200 & 300 > 50";
        String result = XssCleaner.clean(input);
        assertTrue(result.contains("100"));
        assertTrue(result.contains("200"));
        assertTrue(result.contains("300"));
        assertTrue(result.contains("50"));
    }

    @Test
    void shouldRemoveIframeTag() {
        String result = XssCleaner.clean("<iframe src=\"http://evil.com\"></iframe>");
        assertTrue(result.isEmpty() || !result.contains("<iframe"));
    }

    @Test
    void shouldRemoveObjectTag() {
        String result = XssCleaner.clean("<object data=\"evil.swf\"></object>");
        assertTrue(result.isEmpty() || !result.contains("<object"));
    }

    @Test
    void shouldRemoveSvgWithScript() {
        String result = XssCleaner.clean("<svg onload=\"alert(1)\"></svg>");
        assertTrue(result.isEmpty() || !result.contains("onload"));
    }
}
