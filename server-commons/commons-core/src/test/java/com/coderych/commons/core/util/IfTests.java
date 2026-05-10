package com.coderych.commons.core.util;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class IfTests {

    @Test
    void hasValueShouldReturnFalseForNull() {
        assertFalse(If.hasValue((Object) null));
    }

    @Test
    void hasValueShouldReturnFalseForEmptyString() {
        assertFalse(If.hasValue(""));
    }

    @Test
    void hasValueShouldReturnTrueForNonEmptyString() {
        assertTrue(If.hasValue("hello"));
    }

    @Test
    void hasValueShouldReturnFalseForEmptyCollection() {
        assertFalse(If.hasValue(List.of()));
    }

    @Test
    void hasValueShouldReturnTrueForNonEmptyCollection() {
        assertTrue(If.hasValue(List.of(1)));
    }

    @Test
    void hasValueShouldReturnFalseForEmptyMap() {
        assertFalse(If.hasValue(Map.of()));
    }

    @Test
    void hasValueShouldReturnTrueForNonEmptyMap() {
        assertTrue(If.hasValue(Map.of("k", "v")));
    }

    @Test
    void hasValueShouldReturnFalseForEmptyArray() {
        assertFalse(If.hasValue(new int[0]));
    }

    @Test
    void hasValueShouldReturnTrueForNonEmptyArray() {
        assertTrue(If.hasValue(new int[]{1}));
    }

    @Test
    void hasValueShouldReturnTrueForNonNullObject() {
        assertTrue(If.hasValue(42));
    }

    @Test
    void isEmptyShouldBeInverseOfHasValue() {
        assertTrue(If.isEmpty((Object) null));
        assertFalse(If.isEmpty("hello"));
    }

    @Test
    void isTrueShouldHandleBoolean() {
        assertTrue(If.isTrue(Boolean.TRUE));
        assertFalse(If.isTrue(Boolean.FALSE));
    }

    @Test
    void isTrueShouldHandleNumber() {
        assertTrue(If.isTrue(1));
        assertFalse(If.isTrue(2));
        assertFalse(If.isTrue(999));
        assertFalse(If.isTrue(0));
        assertFalse(If.isTrue(-1));
    }

    @Test
    void isTrueShouldHandleString() {
        assertTrue(If.isTrue("true"));
        assertTrue(If.isTrue("True"));
        assertTrue(If.isTrue("TRUE"));
        assertTrue(If.isTrue("1"));
        assertTrue(If.isTrue("yes"));
        assertTrue(If.isTrue("Yes"));
        assertFalse(If.isTrue("false"));
        assertFalse(If.isTrue("0"));
        assertFalse(If.isTrue("no"));
        assertFalse(If.isTrue("other"));
    }

    @Test
    void isTrueShouldReturnFalseForNull() {
        assertFalse(If.isTrue((Object) null));
        assertTrue(If.isNotTrue((Object) null));
    }

    @Test
    void isTrueShouldReturnFalseForUnsupportedTypes() {
        assertFalse(If.isTrue(new Object()));
    }

    @Test
    void isFalseShouldHandleBoolean() {
        assertTrue(If.isFalse(Boolean.FALSE));
        assertFalse(If.isFalse(Boolean.TRUE));
    }

    @Test
    void isFalseShouldHandleNumber() {
        assertTrue(If.isFalse(0));
        assertFalse(If.isFalse(1));
    }

    @Test
    void isFalseShouldHandleString() {
        assertTrue(If.isFalse("false"));
        assertTrue(If.isFalse("False"));
        assertTrue(If.isFalse("0"));
        assertTrue(If.isFalse("no"));
        assertFalse(If.isFalse("true"));
        assertFalse(If.isFalse("1"));
        assertFalse(If.isFalse("other"));
    }

    @Test
    void isFalseShouldReturnFalseForNull() {
        assertFalse(If.isFalse((Object) null));
        assertTrue(If.isNotFalse((Object) null));
    }

    @Test
    void isFalseShouldReturnFalseForUnsupportedTypes() {
        assertFalse(If.isFalse(new Object()));
    }

    @Test
    void isNotTrueShouldBeInverseOfIsTrue() {
        assertTrue(If.isNotTrue(Boolean.FALSE));
        assertFalse(If.isNotTrue(Boolean.TRUE));
    }

    @Test
    void isNotFalseShouldBeInverseOfIsFalse() {
        assertTrue(If.isNotFalse(Boolean.TRUE));
        assertFalse(If.isNotFalse(Boolean.FALSE));
    }

    @Test
    void equalsShouldHandleNull() {
        assertTrue(If.isEqual(null, null));
        assertFalse(If.isEqual(null, "a"));
        assertFalse(If.isEqual("a", null));
    }

    @Test
    void equalsShouldHandleSameReference() {
        String s = "hello";
        assertTrue(If.isEqual(s, s));
    }

    @Test
    void equalsShouldHandleEqualObjects() {
        assertTrue(If.isEqual("hello", "hello"));
        assertFalse(If.isEqual("hello", "world"));
    }

    @Test
    void equalsShouldUseCompareToForBigDecimal() {
        BigDecimal a = new BigDecimal("1.0");
        BigDecimal b = new BigDecimal("1.00");
        assertTrue(If.isEqual(a, b));
        assertFalse(If.isEqual(a, new BigDecimal("2.0")));
    }

    @Test
    void notEqualsShouldBeInverseOfEquals() {
        assertTrue(If.notEquals("a", "b"));
        assertFalse(If.notEquals("a", "a"));
    }

    @Test
    void thenShouldExecuteWhenTrue() {
        boolean[] executed = {false};
        If.then(true, () -> executed[0] = true);
        assertTrue(executed[0]);
    }

    @Test
    void thenShouldNotExecuteWhenFalse() {
        boolean[] executed = {false};
        If.then(false, () -> executed[0] = true);
        assertFalse(executed[0]);
    }

    @Test
    void thenElseShouldExecuteThenWhenTrue() {
        String[] result = {""};
        If.thenElse(true, () -> result[0] = "then", () -> result[0] = "else");
        assertEquals("then", result[0]);
    }

    @Test
    void thenElseShouldExecuteElseWhenFalse() {
        String[] result = {""};
        If.thenElse(false, () -> result[0] = "then", () -> result[0] = "else");
        assertEquals("else", result[0]);
    }

    @Test
    void notNullShouldConsumeWhenNonNull() {
        String[] result = {""};
        If.notNull("hello", v -> result[0] = v);
        assertEquals("hello", result[0]);
    }

    @Test
    void notNullShouldNotConsumeWhenNull() {
        String[] result = {""};
        If.<String>notNull(null, v -> result[0] = v);
        assertEquals("", result[0]);
    }

    @Test
    void getShouldReturnSupplierValueWhenTrue() {
        assertEquals("yes", If.get(true, () -> "yes", "no"));
    }

    @Test
    void getShouldReturnDefaultWhenFalse() {
        assertEquals("no", If.get(false, () -> "yes", "no"));
    }
}
