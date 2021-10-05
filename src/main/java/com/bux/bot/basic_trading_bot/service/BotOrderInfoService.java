package com.bux.bot.basic_trading_bot.service;

import com.bux.bot.basic_trading_bot.exception.EntityValidationException;
import com.bux.bot.basic_trading_bot.model.BotOrderInfo;
import com.bux.bot.basic_trading_bot.model.enums.BotOrderStatus;
import com.bux.bot.basic_trading_bot.repository.BotOrderInfoRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Optional;

import static com.bux.bot.basic_trading_bot.model.enums.BotOrderStatus.*;

@Service
public class BotOrderInfoService {

  public static final String ENTITY_NAME = "BotOrderInfo";
  final BotOrderInfoRepository botOrderInfoRepository;

  public BotOrderInfoService(BotOrderInfoRepository botOrderInfoRepository) {
    this.botOrderInfoRepository = botOrderInfoRepository;
  }

  public Mono<BotOrderInfo> addNewBotOrderInfo(BotOrderInfo botOrderInfo) {
    try {
      validateBotOrderInfo(botOrderInfo);
    } catch (EntityValidationException e) {
      return Mono.error(e);
    }
    botOrderInfo.setStatus(BotOrderStatus.ACTIVE);
    return Mono.just(botOrderInfoRepository.save(botOrderInfo));
  }

  public Mono<BotOrderInfo> updateBotOrderInfo(BotOrderInfo botOrderInfo) {

    try {
      validateBotOrderInfo(botOrderInfo);
    } catch (EntityValidationException e) {
      return Mono.error(e);
    }
    if (botOrderInfo.getId() == null)
      return Mono.error(
          new EntityValidationException(ENTITY_NAME, ValidationMessages.ID_FIELD_IS_NULL));
    Optional<BotOrderInfo> oldBotOrderInfo = botOrderInfoRepository.findById(botOrderInfo.getId());
    if (oldBotOrderInfo.isPresent())
      return Mono.error(
          new EntityValidationException(ENTITY_NAME, ValidationMessages.ID_IS_NOT_VALID));
    try {
      validateBotOrderInfoStateTransition(oldBotOrderInfo.get(), botOrderInfo);
    } catch (EntityValidationException e) {
      return Mono.error(e);
    }
    return Mono.just(botOrderInfoRepository.save(botOrderInfo));
  }

  public void validateBotOrderInfo(BotOrderInfo botOrderInfo) throws EntityValidationException {
    EntityValidationException exception = new EntityValidationException(ENTITY_NAME);
    // checking constraints
    if (botOrderInfo == null) {
      exception.setMessage(ValidationMessages.NULL_ENTITY_NOT_VALID);
      throw exception;
    }
    if (botOrderInfo.getBuyPrice() == null) {
      exception.addError("buyPrice", ValidationMessages.NULL_VALUE_IS_NOT_VALID);
      throw exception;
    }
    if (botOrderInfo.getUpperSellPrice() == null) {
      exception.addError("upperSellPrice", ValidationMessages.NULL_VALUE_IS_NOT_VALID);
      throw exception;
    }
    if (botOrderInfo.getLowerSellPrice() == null) {
      exception.addError("lowerSellPrice", ValidationMessages.NULL_VALUE_IS_NOT_VALID);
      throw exception;
    }
    if (botOrderInfo.getBuyPrice() < botOrderInfo.getLowerSellPrice())
      exception.addError(
          "lowerSellPrice", ValidationMessages.LOWER_SELL_PRICE_MUST_BE_LOWER_THAN_BUY_PRICE);
    if (botOrderInfo.getBuyPrice() > botOrderInfo.getUpperSellPrice())
      exception.addError(
          "upperSellPrice", ValidationMessages.UPPER_SELL_PRICE_MUST_BE_HIGHER_THAN_BUY_PRICE);
    // check if it is valid or not
    if (!exception.isValid()) throw exception;
  }

  public void validateBotOrderInfoStateTransition(
      BotOrderInfo oldBotOrderInfo, BotOrderInfo newBotOrderInfo) throws EntityValidationException {
    BotOrderStatus fromStatus = oldBotOrderInfo.getStatus();
    BotOrderStatus toStatus = newBotOrderInfo.getStatus();
    EntityValidationException exception = new EntityValidationException(ENTITY_NAME);
    if (CLOSED.equals(toStatus) && !OPEN.equals(fromStatus))
      exception.setMessage(ValidationMessages.ONLY_OPEN_ORDER_CAN_CHANGE_TO_CLOSE_STATE);
    if (ACTIVE.equals(toStatus) && !ACTIVE.equals(fromStatus))
      exception.setMessage(ValidationMessages.ACTIVE_STATE_IS_INIT_STATE);
    if (OPEN.equals(toStatus) && !ACTIVE.equals(fromStatus))
      exception.setMessage(ValidationMessages.ONLY_ACTIVE_ORDER_CAN_CHANGE_TO_OPEN_STATE);
    if (CANCELED.equals(toStatus) && !ACTIVE.equals(fromStatus))
      exception.setMessage(ValidationMessages.ONLY_ACTIVE_ORDER_CAN_CHANGE_TO_CANCELED_STATE);
    if (EXPIRED.equals(toStatus) && !ACTIVE.equals(fromStatus))
      exception.setMessage(ValidationMessages.ONLY_ACTIVE_ORDER_CAN_CHANGE_TO_EXPIRED_STATE);
    // check if it is valid or not
    if (!exception.isValid()) throw exception;
  }
}
