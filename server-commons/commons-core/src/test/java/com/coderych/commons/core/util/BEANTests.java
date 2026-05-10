package com.coderych.commons.core.util;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BEANTests {

    @AfterEach
    void tearDown() {
        BEAN.reset();
    }

    @Test
    void shouldConvertMapAndList() {
        Demo demo = BEAN.convert(Map.of("name", "YCH", "age", 18), Demo.class);
        List<Demo> demos = BEAN.convertList(List.of(new Demo("A", 1), new Demo("B", 2)), Demo.class);

        assertEquals("YCH", demo.name());
        assertEquals(18, demo.age());
        assertEquals(2, demos.size());
    }

    record Demo(String name, int age) {
    }
}
