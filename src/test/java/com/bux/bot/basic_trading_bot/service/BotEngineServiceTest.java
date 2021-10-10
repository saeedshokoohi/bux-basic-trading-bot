package com.bux.bot.basic_trading_bot.service;

import com.bux.bot.basic_trading_bot.client.rest.TradeService;
import com.bux.bot.basic_trading_bot.dto.ClosePositionResponse;
import com.bux.bot.basic_trading_bot.dto.OpenPositionResponse;
import com.bux.bot.basic_trading_bot.dto.ProductPrice;
import com.bux.bot.basic_trading_bot.entity.BotOrderInfo;
import com.bux.bot.basic_trading_bot.entity.enums.BotOrderStatus;
import com.bux.bot.basic_trading_bot.exception.InvalidBodyRequestException;
import com.bux.bot.basic_trading_bot.exception.InvalidBrokerConfigurationException;
import com.bux.bot.basic_trading_bot.exception.WebClientInitializationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ContextConfiguration(classes = {BotEngineService.class})
@ExtendWith(SpringExtension.class)
class BotEngineServiceTest {

  @MockBean private TradeService tradeService;
  @MockBean private BotOrderInfoService botOrderInfoService;
  @Autowired private BotEngineService botEngineService;

  @BeforeEach
  public void init() {

    //      this.botEngineService = new BotEngineService(tradeService, botOrderInfoService);
  }

  @Test
  void testCheckPrice_WhenActiveBotOrderLeadsToOpenPosition()
      throws WebClientInitializationException, InvalidBodyRequestException,
          InvalidBrokerConfigurationException {
    // given
    OpenPositionResponse openPositionResponse = new OpenPositionResponse();
    openPositionResponse.setPositionId("rwer");
    Mono<OpenPositionResponse> openPositionResponseMono =
        Mono.create(
            emitter -> {
              emitter.success(openPositionResponse);
            });
    BotOrderInfo botOrderInfo =
        new BotOrderInfo(
            3l, "oldOrder", "ab23423k", "1.0", 12.4, 14.1, 11.1, BotOrderStatus.ACTIVE, "p1", "");
    BotOrderInfo botOrderInfo2 =
        new BotOrderInfo(
            4l, "oldOrder", "ab23423k", "1.0", 12.4, 14.1, 11.1, BotOrderStatus.ACTIVE, "p1", "");

    when(this.tradeService.openLongPosition(
            eq(botOrderInfo.getProductId()),
            eq(botOrderInfo.getAmount()),
            anyInt(),
            anyInt(),
            any()))
        .thenReturn(openPositionResponseMono);
    when(botOrderInfoService.findById(botOrderInfo.getId())).thenReturn(Optional.of(botOrderInfo));
    when(botOrderInfoService.findById(botOrderInfo2.getId()))
        .thenReturn(Optional.of(botOrderInfo2));
    ProductPrice productPriceLowerThanBuyPrice = new ProductPrice("ab23423k", "12.1");
    ProductPrice productPriceEqualToBuyPrice = new ProductPrice("ab23423k", "12.4");
    // when
    Mono<BotOrderInfo> resultWhenLowerThanBuyPrice =
        this.botEngineService.checkPrice(botOrderInfo, productPriceLowerThanBuyPrice);
    Mono<BotOrderInfo> resultWhenLowerEqualToBuyPrice =
        this.botEngineService.checkPrice(botOrderInfo, productPriceEqualToBuyPrice);
    // then

    StepVerifier.create(resultWhenLowerThanBuyPrice).expectNext(botOrderInfo);
    StepVerifier.create(resultWhenLowerEqualToBuyPrice).expectNext(botOrderInfo2);
    verify(this.tradeService)
        .openLongPosition(
            eq(botOrderInfo.getProductId()),
            eq(botOrderInfo.getAmount()),
            anyInt(),
            anyInt(),
            any());
    verify(this.tradeService)
        .openLongPosition(
            eq(botOrderInfo2.getProductId()),
            eq(botOrderInfo2.getAmount()),
            anyInt(),
            anyInt(),
            any());
  }

  @Test
  void testCheckPrice_WhenActiveBotOrderNotleadsToOpenPosition()
      throws WebClientInitializationException, InvalidBodyRequestException,
          InvalidBrokerConfigurationException {
    // given
    String productId = "jh76773weweweee6";
    OpenPositionResponse openPositionResponse = new OpenPositionResponse();
    openPositionResponse.setPositionId("2342312423");
    Mono<OpenPositionResponse> openPositionResponseMono =
        Mono.create(
            emitter -> {
              emitter.success(openPositionResponse);
            });
    BotOrderInfo botOrderInfo =
        new BotOrderInfo(
            45l, "oldOrder", productId, "1.0", 12.4, 14.1, 12.1, BotOrderStatus.ACTIVE, "p1", "");
    when(this.tradeService.openLongPosition(
            eq(botOrderInfo.getProductId()),
            eq(botOrderInfo.getAmount()),
            anyInt(),
            anyInt(),
            any()))
        .thenReturn(openPositionResponseMono);
    when(botOrderInfoService.findById(any())).thenReturn(Optional.of(botOrderInfo));
    ProductPrice productPriceLowerThanBuyPrice = new ProductPrice(productId, "12.8");
    ProductPrice productPriceEqualToBuyPrice = new ProductPrice(productId, "11.4");
    // when
    Mono<BotOrderInfo> resultWhenLowerThanBuyPrice =
        this.botEngineService.checkPrice(botOrderInfo, productPriceLowerThanBuyPrice);
    Mono<BotOrderInfo> resultWhenLowerEqualToBuyPrice =
        this.botEngineService.checkPrice(botOrderInfo, productPriceEqualToBuyPrice);
    // then

    StepVerifier.create(resultWhenLowerThanBuyPrice).expectNext(botOrderInfo);
    StepVerifier.create(resultWhenLowerEqualToBuyPrice).expectNext(botOrderInfo);
    verify(this.tradeService, never())
        .openLongPosition(
            eq(botOrderInfo.getProductId()),
            eq(botOrderInfo.getAmount()),
            anyInt(),
            anyInt(),
            any());
  }

  @Test
  void testCheckPrice_WhenOpenBotOrderLeadsToClosePosition()
      throws WebClientInitializationException, InvalidBodyRequestException,
          InvalidBrokerConfigurationException {
    // given
    ClosePositionResponse closePositionResponse = new ClosePositionResponse();
    closePositionResponse.setPositionId("2r341q2434r");

    BotOrderInfo botOrderInfo =
        new BotOrderInfo(
            null, "oldOrder", "ab23423k", "1.0", 12.4, 14.1, 12.1, BotOrderStatus.OPEN, "p1", "");
    BotOrderInfo botOrderInfo2 =
        new BotOrderInfo(
            null, "oldOrder", "ab23423k", "1.0", 12.4, 14.1, 12.1, BotOrderStatus.OPEN, "p1", "");
    when(this.tradeService.closePosition(any())).thenReturn(Mono.just(closePositionResponse));
    when(botOrderInfoService.findById(any())).thenReturn(Optional.of(botOrderInfo));
    ProductPrice productPricehigherThanUpperSellPrice = new ProductPrice("ab23423k", "16.8");
    ProductPrice productPriceLowerThanLoweSellPrice = new ProductPrice("ab23423k", "11.8");

    // when
    Mono<BotOrderInfo> resultWhenproductPriceLowerThanLoweSellPrice =
        this.botEngineService.checkPrice(botOrderInfo, productPricehigherThanUpperSellPrice);
    Mono<BotOrderInfo> resultWhenproductPricehigherThanUpperSellPrice =
        this.botEngineService.checkPrice(botOrderInfo2, productPriceLowerThanLoweSellPrice);

    // then

    StepVerifier.create(resultWhenproductPriceLowerThanLoweSellPrice).expectNext(botOrderInfo);
    StepVerifier.create(resultWhenproductPricehigherThanUpperSellPrice).expectNext(botOrderInfo2);
    verify(this.tradeService, times(2)).closePosition(any());
  }

  @Test
  void testCheckPrice_WhenOpenBotOrderNotleadsToOpenPosition()
      throws WebClientInitializationException, InvalidBodyRequestException,
          InvalidBrokerConfigurationException {
    // given
    ClosePositionResponse closePositionResponse = new ClosePositionResponse();
    closePositionResponse.setPositionId("2r341q243wew4r");

    BotOrderInfo botOrderInfo =
        new BotOrderInfo(
            null, "oldOrder", "ab23423k", "1.0", 12.4, 14.1, 12.1, BotOrderStatus.OPEN, "p1", "");
    BotOrderInfo botOrderInfo2 =
        new BotOrderInfo(
            null, "oldOrder", "ab23423k", "1.0", 12.4, 14.1, 12.1, BotOrderStatus.OPEN, "p1", "");
    when(this.tradeService.closePosition(any())).thenReturn(Mono.just(closePositionResponse));
    when(botOrderInfoService.findById(any())).thenReturn(Optional.of(botOrderInfo));
    ProductPrice productPricehigherThanUpperSellPrice = new ProductPrice("ab23423k", "12.8");
    ProductPrice productPriceLowerThanLoweSellPrice = new ProductPrice("ab23423k", "13.8");

    // when
    Mono<BotOrderInfo> resultWhenproductPriceHigherThanLoweSellPrice =
        this.botEngineService.checkPrice(botOrderInfo, productPricehigherThanUpperSellPrice);
    Mono<BotOrderInfo> resultWhenproductPriceLowerThanUpperSellPrice =
        this.botEngineService.checkPrice(botOrderInfo2, productPriceLowerThanLoweSellPrice);

    // then

    StepVerifier.create(resultWhenproductPriceHigherThanLoweSellPrice).expectNext(botOrderInfo);
    StepVerifier.create(resultWhenproductPriceLowerThanUpperSellPrice).expectNext(botOrderInfo2);
    verify(this.tradeService, never()).closePosition(closePositionResponse.getPositionId());
  }

  @Test
  void testCheckPrice_WhenBotOrderStatusIsNotValidOpenOrClosePosition()
      throws WebClientInitializationException, InvalidBodyRequestException,
          InvalidBrokerConfigurationException {
    // given
    BotOrderInfo botOrderInfo_CLOSED =
        new BotOrderInfo(
            2l, "oldOrder", "ab23423k", "1.0", 12.0, 14.1, 11.1, BotOrderStatus.CLOSED, "p1", "");
    BotOrderInfo botOrderInfo_CANCELED =
        new BotOrderInfo(
            3l, "oldOrder", "ab23423k", "1.0", 12.0, 14.1, 11.1, BotOrderStatus.CANCELED, "p1", "");
    BotOrderInfo botOrderInfo_EXPIRED =
        new BotOrderInfo(
            4l, "oldOrder", "ab23423k", "1.0", 12.0, 14.1, 11.1, BotOrderStatus.EXPIRED, "p1", "");
    ProductPrice productPrice = new ProductPrice("ab23423k", "12.1");
    when(botOrderInfoService.findById(botOrderInfo_CLOSED.getId()))
        .thenReturn(Optional.of(botOrderInfo_CLOSED));
    when(botOrderInfoService.findById(botOrderInfo_CANCELED.getId()))
        .thenReturn(Optional.of(botOrderInfo_CANCELED));
    when(botOrderInfoService.findById(botOrderInfo_EXPIRED.getId()))
        .thenReturn(Optional.of(botOrderInfo_EXPIRED));
    // when
    Mono<BotOrderInfo> resultForClosedOrder =
        this.botEngineService.checkPrice(botOrderInfo_CLOSED, productPrice);
    Mono<BotOrderInfo> resultForCanceledOrder =
        this.botEngineService.checkPrice(botOrderInfo_CANCELED, productPrice);
    Mono<BotOrderInfo> resultForExpiredOrder =
        this.botEngineService.checkPrice(botOrderInfo_EXPIRED, productPrice);
    // then
    verify(this.tradeService, never())
        .openLongPosition(
            eq(botOrderInfo_CLOSED.getProductId()),
            eq(botOrderInfo_CLOSED.getAmount()),
            anyInt(),
            anyInt(),
            any());
    verify(this.tradeService, never())
        .openLongPosition(
            eq(botOrderInfo_CANCELED.getProductId()),
            eq(botOrderInfo_CANCELED.getAmount()),
            anyInt(),
            anyInt(),
            any());
    verify(this.tradeService, never())
        .openLongPosition(
            eq(botOrderInfo_EXPIRED.getProductId()),
            eq(botOrderInfo_EXPIRED.getAmount()),
            anyInt(),
            anyInt(),
            any());
    verify(this.tradeService, never()).closePosition(any());
  }
}
