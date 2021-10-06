package com.bux.bot.basic_trading_bot.service;

import com.bux.bot.basic_trading_bot.client.rest.bux_impl.TradeService;
import com.bux.bot.basic_trading_bot.dto.ProductPrice;
import com.bux.bot.basic_trading_bot.entity.BotOrderInfo;
import com.bux.bot.basic_trading_bot.entity.enums.BotOrderStatus;
import com.bux.bot.basic_trading_bot.exception.InvalidBodyRequestException;
import com.bux.bot.basic_trading_bot.exception.InvalidBrokerConfigurationException;
import com.bux.bot.basic_trading_bot.exception.WebClientInitializationException;
import org.springframework.stereotype.Service;

@Service
public class BotEngineService {
  final TradeService tradeService;
  final BotOrderInfoService botOrderInfoService;

  public BotEngineService(TradeService tradeService, BotOrderInfoService botOrderInfoService) {
    this.tradeService = tradeService;
    this.botOrderInfoService = botOrderInfoService;
  }

  public boolean checkPrice(BotOrderInfo botOrder, ProductPrice productPrice)
      throws WebClientInitializationException, InvalidBrokerConfigurationException,
          InvalidBodyRequestException {
    if (botOrder == null || productPrice == null) return false;

    BotOrderStatus currentOrderStatus = botOrder.getStatus();
    switch (currentOrderStatus) {
      case ACTIVE:
        return checkPriceForActiveBotOrder(botOrder, productPrice);
      case OPEN:
        return checkPriceForOpenBotOrder(botOrder, productPrice);
    }
    return false;
  }

  private boolean checkPriceForOpenBotOrder(BotOrderInfo botOrder, ProductPrice productPrice)
      throws WebClientInitializationException, InvalidBrokerConfigurationException {

    Double upperSellPrice = botOrder.getUpperSellPrice();
    Double lowerSellPrice = botOrder.getLowerSellPrice();
    Double currentPrice = null;
    try {
      currentPrice = Double.parseDouble(productPrice.getCurrentPrice());
    } catch (NumberFormatException e) {
      e.printStackTrace();
      return false;
    }
    if (upperSellPrice <= currentPrice) {
      closePositionWithProfit(botOrder);
      return true;
    } else if (lowerSellPrice >= currentPrice) {
      closePositionWithLoss(botOrder);
      return true;
    }
    return false;
  }

  private boolean checkPriceForActiveBotOrder(BotOrderInfo botOrder, ProductPrice productPrice)
      throws WebClientInitializationException, InvalidBodyRequestException,
          InvalidBrokerConfigurationException {
    Double buyPrice = botOrder.getBuyPrice();
    Double currentPrice = null;
    try {
      currentPrice = Double.parseDouble(productPrice.getCurrentPrice());
    } catch (NumberFormatException ex) {
      ex.printStackTrace();
      return false;
    }
    if (buyPrice >= currentPrice) {

      openPosition(botOrder);

      return true;
    }

    return false;
  }

  private void openPosition(BotOrderInfo botOrder)
      throws WebClientInitializationException, InvalidBodyRequestException,
          InvalidBrokerConfigurationException {

    this.tradeService
        .openLongPosition(
            botOrder.getProductId(),
            botOrder.getAmount(),
            botOrder.getLeverage(),
            botOrder.getDecimals(),
            botOrder.getCurrency())
        .subscribe(
            position -> {
              if (position != null) {
                botOrderInfoService.openPositionForOrder(botOrder, position);
              }
            });
  }

  private void closePositionWithLoss(BotOrderInfo botOrder)
      throws WebClientInitializationException, InvalidBrokerConfigurationException {
    this.tradeService
        .closePosition(botOrder.getPositionId())
        .subscribe(
            position -> {
              botOrderInfoService.closePositionForOrder(botOrder, position).block();
            });
  }

  private void closePositionWithProfit(BotOrderInfo botOrder)
      throws WebClientInitializationException, InvalidBrokerConfigurationException {
    this.tradeService
        .closePosition(botOrder.getPositionId())
        .subscribe(
            position -> {
              botOrderInfoService.closePositionForOrder(botOrder, position).block();
            });
  }
}
