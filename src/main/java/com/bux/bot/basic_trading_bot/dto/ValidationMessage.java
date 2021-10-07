package com.bux.bot.basic_trading_bot.dto;

import com.bux.bot.basic_trading_bot.exception.ValidationError;

import lombok.Data;

import java.util.Set;

@Data
public class ValidationMessage {
    private String entityName;
    private Set<ValidationError> errors;
    private String message;


    public ValidationMessage(String entityName, Set<ValidationError> errors, String message) {
        this.entityName = entityName;
        this.errors = errors;
        this.message = message;

    }
}
