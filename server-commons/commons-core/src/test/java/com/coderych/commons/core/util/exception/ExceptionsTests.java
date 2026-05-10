package com.coderych.commons.core.util.exception;

import com.coderych.commons.core.exception.InternalException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExceptionsTests {

    @Test
    void uncheckedShouldReturnSameRuntimeException() {
        RuntimeException original = new RuntimeException("test");
        RuntimeException result = Exceptions.unchecked(original);
        assertSame(original, result);
    }

    @Test
    void uncheckedShouldWrapCheckedException() {
        Exception checked = new Exception("checked");
        RuntimeException result = Exceptions.unchecked(checked);
        assertInstanceOf(InternalException.class, result);
        assertEquals("checked", result.getMessage());
        assertSame(checked, result.getCause());
    }

    @Test
    void uncheckedShouldRethrowError() {
        Error error = new OutOfMemoryError("oom");
        assertThrows(OutOfMemoryError.class, () -> Exceptions.unchecked(error));
    }

    @Test
    void rootCauseShouldReturnNullForNull() {
        assertNull(Exceptions.rootCause(null));
    }

    @Test
    void rootCauseShouldReturnSameForNoCause() {
        RuntimeException ex = new RuntimeException("leaf");
        assertSame(ex, Exceptions.rootCause(ex));
    }

    @Test
    void rootCauseShouldReturnDeepestCause() {
        Exception root = new Exception("root");
        Exception mid = new RuntimeException("mid", root);
        Exception top = new RuntimeException("top", mid);
        assertSame(root, Exceptions.rootCause(top));
    }

    @Test
    void rootMessageShouldReturnNullForNull() {
        assertNull(Exceptions.rootMessage(null));
    }

    @Test
    void rootMessageShouldReturnRootMessage() {
        Exception root = new Exception("root cause");
        Exception wrapper = new RuntimeException("wrapper", root);
        assertEquals("root cause", Exceptions.rootMessage(wrapper));
    }

    @Test
    void causedByShouldReturnFalseForNullThrowable() {
        assertFalse(Exceptions.causedBy(null, RuntimeException.class));
    }

    @Test
    void causedByShouldReturnFalseForNullType() {
        assertFalse(Exceptions.causedBy(new RuntimeException("test"), null));
    }

    @Test
    void causedByShouldReturnTrueForDirectMatch() {
        RuntimeException ex = new RuntimeException("test");
        assertTrue(Exceptions.causedBy(ex, RuntimeException.class));
    }

    @Test
    void causedByShouldReturnTrueForCauseMatch() {
        Exception cause = new IllegalArgumentException("cause");
        RuntimeException wrapper = new RuntimeException("wrapper", cause);
        assertTrue(Exceptions.causedBy(wrapper, IllegalArgumentException.class));
    }

    @Test
    void causedByShouldReturnFalseForNoMatch() {
        RuntimeException ex = new RuntimeException("test");
        assertFalse(Exceptions.causedBy(ex, IllegalArgumentException.class));
    }

    @Test
    void causedByShouldCheckDeepCauseChain() {
        Exception deep = new IllegalStateException("deep");
        Exception mid = new RuntimeException("mid", deep);
        Exception top = new RuntimeException("top", mid);
        assertTrue(Exceptions.causedBy(top, IllegalStateException.class));
    }
}
