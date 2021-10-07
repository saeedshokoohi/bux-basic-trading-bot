package com.bux.bot.basic_trading_bot.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

@Data
@AllArgsConstructor
public class ValidationMessage {
    private String entityName;
    private Set<ValidationError> errors;
    private String message;
    private boolean isValid;
}
