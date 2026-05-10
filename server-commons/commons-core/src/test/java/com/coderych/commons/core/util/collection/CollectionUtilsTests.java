package com.coderych.commons.core.util.collection;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class CollectionUtilsTests {

    @Test
    void nullToEmptyListShouldReturnEmptyForNull() {
        List<String> result = CollectionUtils.nullToEmpty((List<String>) null);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void nullToEmptyListShouldReturnSameForNonNull() {
        List<String> original = List.of("a");
        List<String> result = CollectionUtils.nullToEmpty(original);
        assertSame(original, result);
    }

    @Test
    void nullToEmptySetShouldReturnEmptyForNull() {
        Set<String> result = CollectionUtils.nullToEmpty((Set<String>) null);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void nullToEmptySetShouldReturnSameForNonNull() {
        Set<String> original = Set.of("a");
        Set<String> result = CollectionUtils.nullToEmpty(original);
        assertSame(original, result);
    }

    @Test
    void getLastShouldReturnNullForNull() {
        assertNull(CollectionUtils.getLast(null));
    }

    @Test
    void getLastShouldReturnNullForEmpty() {
        assertNull(CollectionUtils.getLast(List.of()));
    }

    @Test
    void getLastShouldReturnLastElementFromList() {
        assertEquals("c", CollectionUtils.getLast(List.of("a", "b", "c")));
    }

    @Test
    void getLastShouldReturnLastElementFromSet() {
        Set<String> set = new LinkedHashSet<>(List.of("a", "b", "c"));
        assertEquals("c", CollectionUtils.getLast(set));
    }

    @Test
    void distinctByShouldReturnEmptyForNull() {
        List<String> result = CollectionUtils.distinctBy(null, String::length);
        assertTrue(result.isEmpty());
    }

    @Test
    void distinctByShouldReturnEmptyForEmpty() {
        List<String> result = CollectionUtils.distinctBy(List.of(), String::length);
        assertTrue(result.isEmpty());
    }

    @Test
    void distinctByShouldKeepFirstOccurrence() {
        List<String> input = List.of("a", "bb", "c", "dd");
        List<String> result = CollectionUtils.distinctBy(input, String::length);
        assertEquals(2, result.size());
        assertEquals("a", result.get(0));
        assertEquals("bb", result.get(1));
    }

    @Test
    void toMapShouldReturnEmptyForNull() {
        Map<String, String> result = CollectionUtils.toMap(null, String::toUpperCase);
        assertTrue(result.isEmpty());
    }

    @Test
    void toMapShouldReturnEmptyForEmpty() {
        Map<String, String> result = CollectionUtils.toMap(List.of(), String::toUpperCase);
        assertTrue(result.isEmpty());
    }

    @Test
    void toMapWithKeyMapperShouldWork() {
        List<String> input = List.of("a", "bb", "ccc");
        Map<Integer, String> result = CollectionUtils.toMap(input, String::length);
        assertEquals(3, result.size());
        assertEquals("a", result.get(1));
        assertEquals("bb", result.get(2));
        assertEquals("ccc", result.get(3));
    }

    @Test
    void toMapWithKeyAndValueMapperShouldWork() {
        List<String> input = List.of("a", "bb", "ccc");
        Map<Integer, Integer> result = CollectionUtils.toMap(input, String::length, String::length);
        assertEquals(3, result.size());
        assertEquals(1, result.get(1));
    }

    @Test
    void linkedHashSetOfShouldReturnEmptyForNull() {
        Set<String> result = CollectionUtils.linkedHashSetOf(null);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void linkedHashSetOfShouldPreserveOrder() {
        Set<String> result = CollectionUtils.linkedHashSetOf(List.of("c", "a", "b"));
        List<String> list = new ArrayList<>(result);
        assertEquals(List.of("c", "a", "b"), list);
    }

    @Test
    void linkedHashSetOfShouldDeduplicate() {
        Set<String> result = CollectionUtils.linkedHashSetOf(List.of("a", "b", "a"));
        assertEquals(2, result.size());
    }
}
