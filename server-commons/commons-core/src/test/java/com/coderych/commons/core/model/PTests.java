package com.coderych.commons.core.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PTests {

    @Test
    void ofShouldReturnEmptyPage() {
        P<String> p = P.of();
        assertEquals(0L, p.getCurrent());
        assertEquals(10L, p.getSize());
        assertEquals(0L, p.getPages());
        assertEquals(0L, p.getTotal());
        assertNotNull(p.getRecords());
        assertTrue(p.getRecords().isEmpty());
    }

    @Test
    void ofWithRecordsShouldReturnFirstPage() {
        P<String> p = P.of(List.of("a", "b", "c"));
        assertEquals(1L, p.getCurrent());
        assertEquals(10L, p.getSize());
        assertEquals(1L, p.getPages());
        assertEquals(3L, p.getTotal());
        assertEquals(List.of("a", "b", "c"), p.getRecords());
    }

    @Test
    void ofWithPageParamsShouldCalculatePages() {
        P<String> p = P.of(2L, 5L, 12L, List.of("a", "b"));
        assertEquals(2L, p.getCurrent());
        assertEquals(5L, p.getSize());
        assertEquals(3L, p.getPages());
        assertEquals(12L, p.getTotal());
    }

    @Test
    void ofWithPageParamsShouldHandleExactDivision() {
        P<String> p = P.of(1L, 10L, 20L, List.of());
        assertEquals(2L, p.getPages());
    }

    @Test
    void hasRecordsShouldReturnTrueForNonEmptyList() {
        P<String> p = P.of(List.of("x"));
        assertTrue(p.hasRecords());
    }

    @Test
    void hasRecordsShouldReturnFalseForEmptyList() {
        P<String> p = P.of();
        assertFalse(p.hasRecords());
    }

    @Test
    void hasRecordsShouldReturnFalseForNullRecords() {
        P<String> p = new P<>(1L, 10L, 1L, 0L, null);
        assertFalse(p.hasRecords());
    }

    @Test
    void isFirstPageShouldReturnTrueWhenCurrentIsNull() {
        P<String> p = new P<>(null, 10L, 1L, 0L, List.of());
        assertTrue(p.isFirstPage());
    }

    @Test
    void isFirstPageShouldReturnTrueWhenCurrentIsOne() {
        P<String> p = new P<>(1L, 10L, 1L, 0L, List.of());
        assertTrue(p.isFirstPage());
    }

    @Test
    void isFirstPageShouldReturnFalseWhenCurrentIsGreaterThanOne() {
        P<String> p = new P<>(2L, 10L, 2L, 20L, List.of());
        assertFalse(p.isFirstPage());
    }

    @Test
    void isLastPageShouldReturnTrueWhenCurrentEqualsPages() {
        P<String> p = new P<>(3L, 10L, 3L, 25L, List.of());
        assertTrue(p.isLastPage());
    }

    @Test
    void isLastPageShouldReturnTrueWhenCurrentExceedsPages() {
        P<String> p = new P<>(5L, 10L, 3L, 25L, List.of());
        assertTrue(p.isLastPage());
    }

    @Test
    void isLastPageShouldReturnFalseWhenCurrentIsLessThanPages() {
        P<String> p = new P<>(1L, 10L, 3L, 25L, List.of());
        assertFalse(p.isLastPage());
    }

    @Test
    void isLastPageShouldReturnFalseWhenPagesIsNull() {
        P<String> p = new P<>(1L, 10L, null, 0L, List.of());
        assertFalse(p.isLastPage());
    }
}
