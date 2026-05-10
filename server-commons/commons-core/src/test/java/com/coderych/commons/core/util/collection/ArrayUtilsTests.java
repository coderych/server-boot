package com.coderych.commons.core.util.collection;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ArrayUtilsTests {

    @Test
    void nullToEmptyShouldReturnEmptyArrayForNull() {
        String[] result = ArrayUtils.nullToEmpty(null, String.class);
        assertNotNull(result);
        assertEquals(0, result.length);
    }

    @Test
    void nullToEmptyShouldReturnSameArrayForNonNull() {
        String[] original = {"a", "b"};
        String[] result = ArrayUtils.nullToEmpty(original, String.class);
        assertSame(original, result);
    }

    @Test
    void firstShouldReturnNullForNullArray() {
        assertNull(ArrayUtils.first((String[]) null));
    }

    @Test
    void firstShouldReturnNullForEmptyArray() {
        assertNull(ArrayUtils.first(new String[0]));
    }

    @Test
    void firstShouldReturnFirstElement() {
        String[] arr = {"a", "b", "c"};
        assertEquals("a", ArrayUtils.first(arr));
    }

    @Test
    void lastShouldReturnNullForNullArray() {
        assertNull(ArrayUtils.last((String[]) null));
    }

    @Test
    void lastShouldReturnNullForEmptyArray() {
        assertNull(ArrayUtils.last(new String[0]));
    }

    @Test
    void lastShouldReturnLastElement() {
        String[] arr = {"a", "b", "c"};
        assertEquals("c", ArrayUtils.last(arr));
    }

    @Test
    void firstAndLastShouldBeSameForSingleElement() {
        String[] arr = {"only"};
        assertEquals("only", ArrayUtils.first(arr));
        assertEquals("only", ArrayUtils.last(arr));
    }
}
