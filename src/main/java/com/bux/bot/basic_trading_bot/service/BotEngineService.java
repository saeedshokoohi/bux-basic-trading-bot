package com.bux.bot.basic_trading_bot.service;

import com.bux.bot.basic_trading_bot.client.rest.TradeClientService;
import com.bux.bot.basic_trading_bot.dto.ProductPrice;
import com.bux.bot.basic_trading_bot.model.BotOrderInfo;
import com.bux.bot.basic_trading_bot.model.enums.BotOrderStatus;
import org.springframework.stereotype.Service;

@Service
public class BotEngineService {
  final TradeClientService tradeClientService;

  public BotEngineService(TradeClientService tradeClientService) {
    this.tradeClientService = tradeClientService;
  }

  public boolean checkPrice(BotOrderInfo botOrder, ProductPrice productPrice) {
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

  private boolean checkPriceForOpenBotOrder(BotOrderInfo botOrder, ProductPrice productPrice) {

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

  private boolean checkPriceForActiveBotOrder(BotOrderInfo botOrder, ProductPrice productPrice) {
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

  private void openPosition(BotOrderInfo botOrder) {

  }

  private void closePositionWithLoss(BotOrderInfo botOrder) {}

  private void closePositionWithProfit(BotOrderInfo botOrder) {}
}
