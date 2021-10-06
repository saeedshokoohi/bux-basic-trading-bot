package com.bux.bot.basic_trading_bot.service;

import com.bux.bot.basic_trading_bot.dto.ClosePositionResponse;
import com.bux.bot.basic_trading_bot.dto.OpenPositionResponse;
import com.bux.bot.basic_trading_bot.entity.BotOrderInfo;
import com.bux.bot.basic_trading_bot.entity.OrderClosePosition;
import com.bux.bot.basic_trading_bot.entity.OrderOpenPosition;
import com.bux.bot.basic_trading_bot.entity.enums.BotOrderStatus;
import com.bux.bot.basic_trading_bot.exception.EntityValidationException;
import com.bux.bot.basic_trading_bot.repository.BotOrderInfoRepository;
import com.bux.bot.basic_trading_bot.repository.OrderClosePositionRepository;
import com.bux.bot.basic_trading_bot.repository.OrderOpenPositionRepository;
import com.bux.bot.basic_trading_bot.service.constants.ValidationMessages;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

import static com.bux.bot.basic_trading_bot.entity.enums.BotOrderStatus.*;

@Service
public class BotOrderInfoService {

  public static final String ENTITY_NAME = "BotOrderInfo";
  final BotOrderInfoRepository botOrderInfoRepository;
  final OrderOpenPositionRepository orderOpenPositionRepository;
  final OrderClosePositionRepository orderClosePositionRepository;

  public BotOrderInfoService(
      BotOrderInfoRepository botOrderInfoRepository,
      OrderOpenPositionRepository orderOpenPositionRepository,
      OrderClosePositionRepository orderClosePositionRepository) {
    this.botOrderInfoRepository = botOrderInfoRepository;
    this.orderOpenPositionRepository = orderOpenPositionRepository;
    this.orderClosePositionRepository = orderClosePositionRepository;
  }

  public Flux<BotOrderInfo> findByStatuses(List<BotOrderStatus> statuses) {
    return Flux.fromIterable(botOrderInfoRepository.findByStatusIn(statuses));
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


  public Mono<BotOrderInfo> openPositionForOrder(BotOrderInfo botOrder, OpenPositionResponse position) {
    OrderOpenPosition orderOpenPosition = mapToOrderOpenPosition(position);
    orderOpenPosition.setOrderId(botOrder.getId());
    botOrder.setStatus(OPEN);
    botOrder.setOpenPosition(orderOpenPosition);
   return updateBotOrderInfo(botOrder);
  }

  public Mono<BotOrderInfo> closePositionForOrder(BotOrderInfo botOrder, ClosePositionResponse position) {
    OrderClosePosition orderClosePosition = mapToOrderClosePosition(position);
    orderClosePosition.setOrderId(botOrder.getId());
    botOrder.setStatus(CLOSED);
    botOrder.setClosePosition(orderClosePosition);
    return updateBotOrderInfo(botOrder);

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
    if (!oldBotOrderInfo.isPresent())
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

  private OrderOpenPosition mapToOrderOpenPosition(OpenPositionResponse position) {
    OrderOpenPosition retOrderOpenPosition = new OrderOpenPosition();
    retOrderOpenPosition.setPositionId(position.getPositionId());
    retOrderOpenPosition.setDirection(position.getDirection());
    retOrderOpenPosition.setDateCreated(position.getDateCreated());
    if (position.getInvestingAmount() != null) {
      retOrderOpenPosition.setInvestingAmount(position.getInvestingAmount().getAmount());
      retOrderOpenPosition.setInvestingAmountCurrency(position.getInvestingAmount().getCurrency());
      retOrderOpenPosition.setInvestingAmountDecimals(position.getInvestingAmount().getDecimals());
    }
    retOrderOpenPosition.setClientId(position.getId());
    retOrderOpenPosition.setLeverage(position.getLeverage());
    if (position.getProduct() != null) {
      retOrderOpenPosition.setProductDisplayName(position.getProduct().getDisplayName());
      retOrderOpenPosition.setProductSecurityId(position.getProduct().getSecurityId());
    }
    if (position.getPrice() != null) {
      retOrderOpenPosition.setType(position.getType());
      retOrderOpenPosition.setPriceCurrency(position.getPrice().getCurrency());
      retOrderOpenPosition.setPriceAmount(position.getPrice().getAmount());
      retOrderOpenPosition.setPriceDecimals(position.getPrice().getDecimals());
    }
    return retOrderOpenPosition;
  }

  private OrderClosePosition mapToOrderClosePosition(ClosePositionResponse position) {
    OrderClosePosition retOrderClosePosition = new OrderClosePosition();
    retOrderClosePosition.setPositionId(position.getPositionId());
    retOrderClosePosition.setDirection(position.getDirection());
    retOrderClosePosition.setDateCreated(position.getDateCreated());
    if (position.getInvestingAmount() != null) {
      retOrderClosePosition.setInvestingAmount(position.getInvestingAmount().getAmount());
      retOrderClosePosition.setInvestingAmountCurrency(position.getInvestingAmount().getCurrency());
      retOrderClosePosition.setInvestingAmountDecimals(position.getInvestingAmount().getDecimals());
    }
    retOrderClosePosition.setClientId(position.getId());
    retOrderClosePosition.setLeverage(position.getLeverage());
    if (position.getProduct() != null) {
      retOrderClosePosition.setProductDisplayName(position.getProduct().getDisplayName());
      retOrderClosePosition.setProductSecurityId(position.getProduct().getSecurityId());
    }
    if (position.getPrice() != null) {
      retOrderClosePosition.setType(position.getType());
      retOrderClosePosition.setPriceCurrency(position.getPrice().getCurrency());
      retOrderClosePosition.setPriceAmount(position.getPrice().getAmount());
      retOrderClosePosition.setPriceDecimals(position.getPrice().getDecimals());
    }
    if (position.getProfitAndLoss() != null) {
      retOrderClosePosition.setProfitAndLossAmount(position.getProfitAndLoss().getAmount());
      retOrderClosePosition.setProfitAndLossCurrency(position.getProfitAndLoss().getCurrency());
      retOrderClosePosition.setProfitAndLossDecimals(position.getProfitAndLoss().getDecimals());
    }
    return retOrderClosePosition;
  }
}
