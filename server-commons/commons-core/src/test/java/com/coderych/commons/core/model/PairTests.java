package com.coderych.commons.core.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PairTests {

    @Test
    void ofShouldCreatePairWithValues() {
        Pair<String, Integer> p = Pair.of("key", 42);
        assertEquals("key", p.getKey());
        assertEquals(42, p.getValue());
    }

    @Test
    void emptyShouldCreatePairWithNulls() {
        Pair<String, Integer> p = Pair.empty();
        assertNull(p.getKey());
        assertNull(p.getValue());
    }

    @Test
    void isEmptyShouldReturnTrueForNullKeyAndValue() {
        Pair<String, String> p = Pair.empty();
        assertTrue(p.isEmpty());
    }

    @Test
    void isEmptyShouldReturnFalseForNonNullKey() {
        Pair<String, String> p = Pair.of("k", null);
        assertFalse(p.isEmpty());
    }

    @Test
    void isEmptyShouldReturnFalseForNonNullValue() {
        Pair<String, String> p = Pair.of(null, "v");
        assertFalse(p.isEmpty());
    }

    @Test
    void isNotEmptyShouldBeInverseOfIsEmpty() {
        Pair<String, String> empty = Pair.empty();
        Pair<String, String> nonEmpty = Pair.of("a", "b");
        assertFalse(empty.isNotEmpty());
        assertTrue(nonEmpty.isNotEmpty());
    }

    @Test
    void getLeftShouldReturnKey() {
        Pair<String, Integer> p = Pair.of("left", 1);
        assertEquals("left", p.getLeft());
    }

    @Test
    void getRightShouldReturnValue() {
        Pair<String, Integer> p = Pair.of("left", 1);
        assertEquals(1, p.getRight());
    }

    @Test
    void getFirstShouldReturnKey() {
        Pair<String, Integer> p = Pair.of("first", 2);
        assertEquals("first", p.getFirst());
    }

    @Test
    void getSecondShouldReturnValue() {
        Pair<String, Integer> p = Pair.of("first", 2);
        assertEquals(2, p.getSecond());
    }

    @Test
    void allArgsConstructorShouldWork() {
        Pair<String, Integer> p = new Pair<>("k", 99);
        assertEquals("k", p.getKey());
        assertEquals(99, p.getValue());
    }

    @Test
    void noArgsConstructorShouldCreateNullPair() {
        Pair<String, Integer> p = new Pair<>();
        assertNull(p.getKey());
        assertNull(p.getValue());
    }
}
