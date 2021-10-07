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
    if (  botOrder == null || botOrder.isProcessing() || productPrice == null) return false;

    BotOrderStatus currentOrderStatus = botOrder.getStatus();
    botOrder.setProcessing(true);
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
    boolean retValue=false;
    Double upperSellPrice = botOrder.getUpperSellPrice();
    Double lowerSellPrice = botOrder.getLowerSellPrice();
    Double currentPrice = null;
    try {
      currentPrice = Double.parseDouble(productPrice.getCurrentPrice());
    } catch (NumberFormatException e) {
      e.printStackTrace();
      retValue= false;
    }
    if (upperSellPrice <= currentPrice) {
      closePositionWithProfit(botOrder);
      retValue= true;
    } else if (lowerSellPrice >= currentPrice) {
      closePositionWithLoss(botOrder);
      retValue= true;
    }
    botOrder.setProcessing(false);
    return retValue;
  }

  private boolean checkPriceForActiveBotOrder(BotOrderInfo botOrder, ProductPrice productPrice)
      throws WebClientInitializationException, InvalidBodyRequestException,
          InvalidBrokerConfigurationException {
    boolean retValue=false;
    Double buyPrice = botOrder.getBuyPrice();
    Double currentPrice = null;
    try {
      currentPrice = Double.parseDouble(productPrice.getCurrentPrice());
    } catch (NumberFormatException ex) {
      ex.printStackTrace();
      retValue= false;
    }
    if (buyPrice >= currentPrice) {

      openPosition(botOrder);

      retValue= true;
    }
    botOrder.setProcessing(false);
    return retValue;
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
                botOrderInfoService.openPositionForOrder(botOrder, position).block();
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
