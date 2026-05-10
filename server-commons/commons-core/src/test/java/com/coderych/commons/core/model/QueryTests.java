package com.coderych.commons.core.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class QueryTests {

    @Test
    void ofShouldReturnEmptyQuery() {
        Query q = Query.of();
        assertNull(q.orderBy());
    }

    @Test
    void parseOrderByShouldReturnEmptyListForNull() {
        Query q = Query.of();
        List<Pair<String, Boolean>> result = q.parseOrderBy();
        assertTrue(result.isEmpty());
    }

    @Test
    void parseOrderByShouldReturnEmptyListForBlank() {
        Query q = new Query().orderBy("   ");
        List<Pair<String, Boolean>> result = q.parseOrderBy();
        assertTrue(result.isEmpty());
    }

    @Test
    void parseOrderByShouldParseSingleFieldAscending() {
        Query q = new Query().orderBy("name asc");
        List<Pair<String, Boolean>> result = q.parseOrderBy();
        assertEquals(1, result.size());
        assertEquals("name", result.get(0).getKey());
        assertTrue(result.get(0).getValue());
    }

    @Test
    void parseOrderByShouldParseSingleFieldDescending() {
        Query q = new Query().orderBy("name desc");
        List<Pair<String, Boolean>> result = q.parseOrderBy();
        assertEquals(1, result.size());
        assertEquals("name", result.get(0).getKey());
        assertFalse(result.get(0).getValue());
    }

    @Test
    void parseOrderByShouldDefaultToAscendingWhenNoDirection() {
        Query q = new Query().orderBy("name");
        List<Pair<String, Boolean>> result = q.parseOrderBy();
        assertEquals(1, result.size());
        assertTrue(result.get(0).getValue());
    }

    @Test
    void parseOrderByShouldParseMultipleFields() {
        Query q = new Query().orderBy("name asc,age desc");
        List<Pair<String, Boolean>> result = q.parseOrderBy();
        assertEquals(2, result.size());
        assertEquals("name", result.get(0).getKey());
        assertTrue(result.get(0).getValue());
        assertEquals("age", result.get(1).getKey());
        assertFalse(result.get(1).getValue());
    }

    @Test
    void parseOrderByShouldBeCaseInsensitive() {
        Query q = new Query().orderBy("name DESC");
        List<Pair<String, Boolean>> result = q.parseOrderBy();
        assertEquals(1, result.size());
        assertFalse(result.get(0).getValue());
    }

    @Test
    void fluentAccessorsShouldWork() {
        Query q = Query.of().orderBy("id asc");
        assertEquals("id asc", q.orderBy());
    }
}
