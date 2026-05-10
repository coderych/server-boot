package com.coderych.commons.core.util.validation;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.constraints.NotBlank;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ValidationUtilsTests {

    @Test
    void shouldValidateWithoutSpringContext() {
        assertDoesNotThrow(() -> ValidationUtils.validate(new Demo("ok")));
        assertTrue(ValidationUtils.isValid(new Demo("ok")));
        assertFalse(ValidationUtils.isValid(new Demo(" ")));
        assertThrows(ConstraintViolationException.class, () -> ValidationUtils.validate(new Demo(" ")));
    }

    record Demo(@NotBlank String name) {
    }
}
