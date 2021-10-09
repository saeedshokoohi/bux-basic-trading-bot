package com.bux.bot.basic_trading_bot.exception;

import com.bux.bot.basic_trading_bot.dto.ValidationMessage;
import com.bux.bot.basic_trading_bot.util.JsonUtil;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.HashSet;
import java.util.Set;

public class EntityValidationException extends Exception {
  private  String entityName;
  private  Set<ValidationError> errors;
  private String message;
  private  boolean isValid = true;

  public EntityValidationException(String entityName) {
    this.entityName = entityName;
  }

  public EntityValidationException(String entityName, String message) {
    this.entityName = entityName;
    this.message = message;
    isValid = false;
  }

  public String getEntityName() {
    return entityName;
  }

  public Set<ValidationError> getErrors() {
    return errors;
  }

  @Override
  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
    isValid = false;
  }

  public void addError(String field, String userError, String developerError) {
    if (errors == null) errors = new HashSet<>();
    errors.add(new ValidationError(field, userError, developerError));
  }

  public void addError(String field, String userError) {
    if (errors == null) errors = new HashSet<>();
    errors.add(new ValidationError(field, userError, userError));
  }

  public boolean isValid() {
    return (isValid && (errors == null || errors.isEmpty()));
  }

  public ValidationMessage getErrorMessage() {
    return new ValidationMessage(entityName, errors, message);
  }

  @Override
  public String toString() {

    try {
      return JsonUtil.toJsonFormat(getErrorMessage());
    } catch (JsonProcessingException e) {
      return super.toString();
    }
  }
}
