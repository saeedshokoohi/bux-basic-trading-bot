package com.bux.bot.basic_trading_bot.service;

import com.bux.bot.basic_trading_bot.client.rest.bux_impl.TradeService;
import com.bux.bot.basic_trading_bot.dto.ProductPrice;
import com.bux.bot.basic_trading_bot.entity.BotOrderInfo;
import com.bux.bot.basic_trading_bot.entity.enums.BotOrderStatus;
import com.bux.bot.basic_trading_bot.exception.InvalidBodyRequestException;
import com.bux.bot.basic_trading_bot.exception.InvalidBrokerConfigurationException;
import com.bux.bot.basic_trading_bot.exception.WebClientInitializationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Service
public class BotEngineService {
  final TradeService tradeService;
  final BotOrderInfoService botOrderInfoService;
  Logger logger = LoggerFactory.getLogger(BotEngineService.class);

  public BotEngineService(TradeService tradeService, BotOrderInfoService botOrderInfoService) {
    this.tradeService = tradeService;
    this.botOrderInfoService = botOrderInfoService;
  }

  public Mono<BotOrderInfo> checkPrice(BotOrderInfo botOrder, ProductPrice productPrice)
      throws WebClientInitializationException, InvalidBrokerConfigurationException,
          InvalidBodyRequestException {

    if (botOrder == null || botOrder.isProcessing() || productPrice == null) return Mono.empty();
    Optional<BotOrderInfo> currentBotOrder = botOrderInfoService.findById(botOrder.getId());
    BotOrderInfo attachedBotOrder = null;
    if (currentBotOrder.isPresent()) attachedBotOrder = currentBotOrder.get();
    else return Mono.empty();
    BotOrderStatus currentOrderStatus = attachedBotOrder.getStatus();

    boolean retValue = false;
    botOrder.setProcessing(true);
    switch (currentOrderStatus) {
      case ACTIVE:
        return checkPriceForActiveBotOrder(attachedBotOrder, productPrice).doOnError(e->{
          botOrder.setProcessing(false);
        }).doOnNext(e->{
          botOrder.setProcessing(false);
        });

      case OPEN:
        return checkPriceForOpenBotOrder(attachedBotOrder, productPrice)
                .doOnError(e->{
                  botOrder.setProcessing(false);
                }).doOnNext(e->{
                  botOrder.setProcessing(false);
                });

    }
    botOrder.setProcessing(false);
    return Mono.empty();
  }

  private Mono<BotOrderInfo> checkPriceForOpenBotOrder(
      BotOrderInfo botOrder, ProductPrice productPrice)
      throws WebClientInitializationException, InvalidBrokerConfigurationException {
    logger.info(
        "checking price to CLOSE  POSITION for botOrder : "
            + botOrder.getId()
            + " on product: "
            + botOrder.getProductId());

    Double upperSellPrice = botOrder.getUpperSellPrice();
    Double lowerSellPrice = botOrder.getLowerSellPrice();
    Double currentPrice = null;
    try {
      currentPrice = Double.parseDouble(productPrice.getCurrentPrice());
    } catch (NumberFormatException e) {
      e.printStackTrace();
    }
    if (upperSellPrice <= currentPrice) {
     return closePositionWithProfit(botOrder);
    } else if (lowerSellPrice >= currentPrice) {
      return closePositionWithLoss(botOrder);
    } else {
      logger.info(
          String.format(
              "not good to sell! not currentPrice>= upperSellPrice :  %f ! > %f nor currentPrice<=lowerSellPrice : %f !<=%f ",
              currentPrice, upperSellPrice, currentPrice, lowerSellPrice));
    }

    return Mono.just(botOrder);
  }

  private Mono<BotOrderInfo> checkPriceForActiveBotOrder(
      BotOrderInfo botOrder, ProductPrice productPrice)
      throws WebClientInitializationException, InvalidBodyRequestException,
          InvalidBrokerConfigurationException {
    logger.info(
        String.format(
            "checking price to OPEN a POSITION for botOrder : %d on product: %s",
            botOrder.getId(), botOrder.getProductId()));
    Double buyPrice = botOrder.getBuyPrice();
    Double currentPrice = null;
    try {
      currentPrice = Double.parseDouble(productPrice.getCurrentPrice());
    } catch (NumberFormatException ex) {
      ex.printStackTrace();
    }
    if (buyPrice >= currentPrice) {
      return openPosition(botOrder);
    } else {
      logger.info(
          String.format(
              "not good to buy! currentPrice> buyPrice :  %f > %f", currentPrice, buyPrice));
    }
    return Mono.just(botOrder);
  }

  private Mono<BotOrderInfo> openPosition(BotOrderInfo botOrder)
      throws WebClientInitializationException, InvalidBodyRequestException,
          InvalidBrokerConfigurationException {
    logger.info(
        String.format(
            "try to open position on product :%s for order:%d",
            botOrder.getProductId(), botOrder.getId()));
    return this.tradeService
        .openLongPosition(
            botOrder.getProductId(),
            botOrder.getAmount(),
            botOrder.getLeverage(),
            botOrder.getDecimals(),
            botOrder.getCurrency())
        .doOnNext(
            position -> {
              logger.info(
                  String.format(
                      "Position Opened  ,positionId : %s , orderId : %s",
                      position.getPositionId(), botOrder.getId()));
            })
        .flatMap(position -> botOrderInfoService.openPositionForOrder(botOrder, position))
        .doOnNext(bo -> String.format("order updated on database orderId : %s", bo.getId()));
  }

  private Mono<BotOrderInfo> closePositionWithLoss(BotOrderInfo botOrder)
      throws WebClientInitializationException, InvalidBrokerConfigurationException {
    logger.info(
        String.format(
            "try to close position with loss on :%sfor order :%d",
            botOrder.getPositionId(), botOrder.getId()));
    return this.tradeService
        .closePosition(botOrder.getPositionId())
        .doOnNext(
            position -> {
              logger.info(
                  String.format(
                      "Position closed with loss ,positionId : %s , orderId : %s",
                      position.getPositionId(), botOrder.getId()));
            })
        .flatMap(position -> botOrderInfoService.closePositionForOrder(botOrder, position))
        .doOnNext(bo -> String.format("order updated on database orderId : %s", bo.getId()));
  }

  private Mono<BotOrderInfo> closePositionWithProfit(BotOrderInfo botOrder)
      throws WebClientInitializationException, InvalidBrokerConfigurationException {
    logger.info(
        "try to close position with profit on :"
            + botOrder.getPositionId()
            + "for order :"
            + botOrder.getId());
   return this.tradeService
        .closePosition(botOrder.getPositionId())
        .doOnNext(
            position ->
                logger.info(
                    String.format(
                        "Position closed with profit ,positionId : %s , orderId : %s",
                        position.getPositionId(), botOrder.getId())))
        .flatMap(position -> botOrderInfoService.closePositionForOrder(botOrder, position))
        .doOnNext(bo -> String.format("order updated on database orderId : %s", bo.getId()));

  }
}
