package com.bux.bot.basic_trading_bot.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ValidationErrorTest {


    @Test
    void testHashCode() {
        //given
        ValidationError validationError = new ValidationError("Field", "An error occurred", "An error occurred");
        ValidationError validationErrorSame = new ValidationError("Field", "An error occurred", "An error occurred");
        ValidationError validationErrorDifferentInField = new ValidationError("Field2", "An error occurred", "An error occurred");
        ValidationError validationErrorDifferentInUserError = new ValidationError("Field", "An error occurred2", "An error occurred");
        ValidationError validationErrorDifferentInDeveloperError = new ValidationError("Field", "An error occurred", "An error occurred2");
        //then
        assertTrue(validationError.equals(validationErrorSame));
        assertFalse(validationError.equals(null));
        assertFalse(validationError.equals(validationErrorDifferentInField));
        assertFalse(validationError.equals(validationErrorDifferentInUserError));
        assertFalse(validationError.equals(validationErrorDifferentInDeveloperError));

    }


}

