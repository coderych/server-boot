package com.coderych.commons.mybatisflex.util;

import com.coderych.commons.core.model.P;
import com.coderych.commons.core.model.PageQuery;
import com.mybatisflex.core.paginate.Page;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class OfTests {

    @Test
    void pShouldConvertPageToP() {
        Page<String> page = mock(Page.class);
        when(page.getPageNumber()).thenReturn(2L);
        when(page.getPageSize()).thenReturn(10L);
        when(page.getTotalRow()).thenReturn(25L);
        when(page.getRecords()).thenReturn(List.of("a", "b", "c"));

        P<String> result = Of.p(page);

        assertEquals(2L, result.getCurrent());
        assertEquals(10L, result.getSize());
        assertEquals(25L, result.getTotal());
        assertEquals(3L, result.getPages());
        assertEquals(List.of("a", "b", "c"), result.getRecords());
    }

    @Test
    void pShouldHandleSinglePage() {
        Page<Integer> page = mock(Page.class);
        when(page.getPageNumber()).thenReturn(1L);
        when(page.getPageSize()).thenReturn(10L);
        when(page.getTotalRow()).thenReturn(5L);
        when(page.getRecords()).thenReturn(List.of(1, 2, 3, 4, 5));

        P<Integer> result = Of.p(page);

        assertEquals(1L, result.getCurrent());
        assertEquals(10L, result.getSize());
        assertEquals(5L, result.getTotal());
        assertEquals(1L, result.getPages());
        assertEquals(5, result.getRecords().size());
    }

    @Test
    void pShouldHandleEmptyRecords() {
        Page<String> page = mock(Page.class);
        when(page.getPageNumber()).thenReturn(1L);
        when(page.getPageSize()).thenReturn(10L);
        when(page.getTotalRow()).thenReturn(0L);
        when(page.getRecords()).thenReturn(List.of());

        P<String> result = Of.p(page);

        assertEquals(1L, result.getCurrent());
        assertEquals(10L, result.getSize());
        assertEquals(0L, result.getTotal());
        assertEquals(0, result.getRecords().size());
    }

    @Test
    void pageShouldConvertPageQueryToPage() {
        PageQuery pageQuery = new PageQuery();
        pageQuery.current(3L);
        pageQuery.size(20L);

        Page<Object> result = Of.page(pageQuery);

        assertEquals(3L, result.getPageNumber());
        assertEquals(20L, result.getPageSize());
    }

    @Test
    void pageShouldUseDefaultValues() {
        PageQuery pageQuery = new PageQuery();

        Page<Object> result = Of.page(pageQuery);

        assertEquals(1L, result.getPageNumber());
        assertEquals(10L, result.getPageSize());
    }

    @Test
    void pShouldCalculatePagesCorrectlyForExactDivision() {
        Page<String> page = mock(Page.class);
        when(page.getPageNumber()).thenReturn(1L);
        when(page.getPageSize()).thenReturn(10L);
        when(page.getTotalRow()).thenReturn(20L);
        when(page.getRecords()).thenReturn(List.of());

        P<String> result = Of.p(page);

        assertEquals(2L, result.getPages());
    }

    @Test
    void pShouldCalculatePagesCorrectlyForRemainder() {
        Page<String> page = mock(Page.class);
        when(page.getPageNumber()).thenReturn(3L);
        when(page.getPageSize()).thenReturn(10L);
        when(page.getTotalRow()).thenReturn(21L);
        when(page.getRecords()).thenReturn(List.of("x"));

        P<String> result = Of.p(page);

        assertEquals(3L, result.getPages());
    }
}
