package com.coderych.commons.core.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PageQueryTests {

    @Test
    void ofShouldReturnDefaultPageQuery() {
        PageQuery pq = PageQuery.of();
        assertEquals(1L, pq.current());
        assertEquals(10L, pq.size());
        assertNull(pq.orderBy());
    }

    @Test
    void getOffsetShouldReturnZeroForFirstPage() {
        PageQuery pq = PageQuery.of();
        assertEquals(0L, pq.getOffset());
    }

    @Test
    void getOffsetShouldCalculateCorrectly() {
        PageQuery pq = new PageQuery().current(3L).size(20L);
        assertEquals(40L, pq.getOffset());
    }

    @Test
    void fluentAccessorsShouldWork() {
        PageQuery pq = PageQuery.of();
        pq.current(5L);
        pq.size(20L);
        pq.orderBy("id asc");
        assertEquals(5L, pq.current());
        assertEquals(20L, pq.size());
        assertEquals("id asc", pq.orderBy());
    }

    @Test
    void parseOrderByShouldInheritFromQuery() {
        PageQuery pq = PageQuery.of();
        pq.orderBy("name desc");
        var result = pq.parseOrderBy();
        assertEquals(1, result.size());
        assertEquals("name", result.get(0).getKey());
        assertFalse(result.get(0).getValue());
    }
}
