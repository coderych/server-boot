package com.coderych.commons.core.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class STRTests {

    @Test
    void emptyToNullShouldReturnNullForNull() {
        assertNull(STR.emptyToNull(null));
    }

    @Test
    void emptyToNullShouldReturnNullForEmpty() {
        assertNull(STR.emptyToNull(""));
    }

    @Test
    void emptyToNullShouldReturnStringForNonEmpty() {
        assertEquals("hello", STR.emptyToNull("hello"));
    }

    @Test
    void nullToEmptyShouldReturnEmptyForNull() {
        assertEquals("", STR.nullToEmpty(null));
    }

    @Test
    void nullToEmptyShouldReturnStringForNonNull() {
        assertEquals("hello", STR.nullToEmpty("hello"));
    }

    @Test
    void blankToNullShouldReturnNullForNull() {
        assertNull(STR.blankToNull(null));
    }

    @Test
    void blankToNullShouldReturnNullForBlank() {
        assertNull(STR.blankToNull("   "));
    }

    @Test
    void blankToNullShouldReturnStringForNonBlank() {
        assertEquals("hello", STR.blankToNull("hello"));
    }

    @Test
    void removeAllBlankShouldReturnNullForNull() {
        assertNull(STR.removeAllBlank(null));
    }

    @Test
    void removeAllBlankShouldRemoveWhitespace() {
        assertEquals("helloworld", STR.removeAllBlank("hello  world"));
    }

    @Test
    void removeAllBlankShouldRemoveTabs() {
        assertEquals("abc", STR.removeAllBlank("\ta\tb\tc"));
    }

    @Test
    void removeAllBlankShouldRemoveNewlines() {
        assertEquals("abc", STR.removeAllBlank("a\nb\nc"));
    }

    @Test
    void joinIfNotBlankShouldJoinNonBlankValues() {
        assertEquals("a,b,c", STR.joinIfNotBlank(",", "a", "b", "c"));
    }

    @Test
    void joinIfNotBlankShouldSkipBlankValues() {
        assertEquals("a,c", STR.joinIfNotBlank(",", "a", "", "c"));
    }

    @Test
    void joinIfNotBlankShouldSkipNullValues() {
        assertEquals("a,c", STR.joinIfNotBlank(",", "a", null, "c"));
    }

    @Test
    void joinIfNotBlankShouldHandleNullDelimiter() {
        assertEquals("abc", STR.joinIfNotBlank(null, "a", "b", "c"));
    }

    @Test
    void joinIfNotBlankShouldReturnEmptyForAllBlank() {
        assertEquals("", STR.joinIfNotBlank(",", "", "  ", null));
    }
}
