package com.bux.bot.basic_trading_bot.service;

import com.bux.bot.basic_trading_bot.client.rest.bux_impl.TradeService;
import com.bux.bot.basic_trading_bot.dto.OpenPositionRequest;
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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;


@ContextConfiguration(classes = {BotEngineService.class})
@ExtendWith(SpringExtension.class)
class BotEngineServiceTest {


  @MockBean private TradeService tradeService;
  @MockBean private BotOrderInfoService botOrderInfoService;
  private BotEngineService botEngineService;
  @BeforeEach
  public void init()
  {
   this.botEngineService=new BotEngineService(tradeService,botOrderInfoService);
  }

  @Test
  void testCheckPrice_WhenActiveBotOrderLeadsToOpenPosition()
      throws WebClientInitializationException, InvalidBodyRequestException,
          InvalidBrokerConfigurationException {
    // given
    OpenPositionResponse openPositionResponse=new OpenPositionResponse();
    openPositionResponse.setPositionId("rwer");
    Mono<OpenPositionResponse> openPositionResponseMono=Mono.create(emitter->{
      emitter.success(openPositionResponse);
    });
    BotOrderInfo botOrderInfo =
        new BotOrderInfo(
            null, "oldOrder", "ab23423k","1.0", 12.4, 14.1, 11.1, BotOrderStatus.ACTIVE, "p1", "");
    when(this.tradeService.openLongPosition(eq(botOrderInfo.getProductId()),eq(botOrderInfo.getAmount()),anyInt(),anyInt(),any())).
            thenReturn(openPositionResponseMono);
    ProductPrice productPriceLowerThanBuyPrice = new ProductPrice("ab23423k", "12.1");
    ProductPrice productPriceEqualToBuyPrice = new ProductPrice("ab23423k", "12.4");
    // when
    boolean resultWhenLowerThanBuyPrice =
        this.botEngineService.checkPrice(botOrderInfo, productPriceLowerThanBuyPrice);
    boolean resultWhenLowerEqualToBuyPrice =
        this.botEngineService.checkPrice(botOrderInfo, productPriceEqualToBuyPrice);
    // then
    assertTrue(resultWhenLowerThanBuyPrice);
    assertTrue(resultWhenLowerEqualToBuyPrice);
  }

  @Test
  void testCheckPrice_WhenActiveBotOrderNotleadsToOpenPosition()
      throws WebClientInitializationException, InvalidBodyRequestException,
          InvalidBrokerConfigurationException {
    // given
    BotOrderInfo botOrderInfo =
        new BotOrderInfo(
            null, "oldOrder", "ab23423k","1.0", 12.0, 14.1, 11.1, BotOrderStatus.ACTIVE, "p1", "");
    ProductPrice productPriceHigherThanBuyPrice = new ProductPrice("ab23423k", "12.1");
    ProductPrice productPriceNotValid = new ProductPrice("ab23423k", "notValid");
    // when
    boolean resultWhenLowerThanBuyPrice =
        this.botEngineService.checkPrice(botOrderInfo, productPriceHigherThanBuyPrice);
    boolean resultWhenNotValidPrice =
        this.botEngineService.checkPrice(botOrderInfo, productPriceNotValid);
    // then
    assertFalse(resultWhenLowerThanBuyPrice);
    assertFalse(resultWhenNotValidPrice);
  }

  @Test
  void testCheckPrice_WhenOpenBotOrderLeadsToClosePosition()
      throws WebClientInitializationException, InvalidBodyRequestException,
          InvalidBrokerConfigurationException {
    // given
    BotOrderInfo botOrderInfo =
        new BotOrderInfo(
            null, "oldOrder", "ab23423k","1.0", 12.4, 14.1, 11.1, BotOrderStatus.OPEN, "p1", "");
    ProductPrice productPriceLowerThanLowerSellPrice = new ProductPrice("ab23423k", "11.0");
    ProductPrice productPriceHigherThanUpperSellPrice = new ProductPrice("ab23423k", "14.4");
    when(this.tradeService.closePosition(any())).
            thenReturn(Mono.empty());
    // when
    boolean resultWhenProductPriceLowerThanLowerSellPrice =
        this.botEngineService.checkPrice(botOrderInfo, productPriceLowerThanLowerSellPrice);
    boolean resultWhenProductPriceHigherThanUpperSellPrice =
        this.botEngineService.checkPrice(botOrderInfo, productPriceHigherThanUpperSellPrice);
    // then
    assertTrue(resultWhenProductPriceLowerThanLowerSellPrice);
    assertTrue(resultWhenProductPriceHigherThanUpperSellPrice);
  }

  @Test
  void testCheckPrice_WhenOpenBotOrderNotleadsToOpenPosition()
      throws WebClientInitializationException, InvalidBodyRequestException,
          InvalidBrokerConfigurationException {
    // given
    BotOrderInfo botOrderInfo =
        new BotOrderInfo(
            null, "oldOrder", "ab23423k","1.0", 12.0, 14.1, 11.1, BotOrderStatus.OPEN, "p1", "");
    ProductPrice productPriceHigherThanLowerSellPrice = new ProductPrice("ab23423k", "11.2");
    ProductPrice productPriceLowerThanUpperSellPrice = new ProductPrice("ab23423k", "14.0");
    ProductPrice productPriceNotValid = new ProductPrice("ab23423k", "notValidPrice");
    // when
    boolean resultWhenProductPriceHigherThanLowerSellPrice =
        this.botEngineService.checkPrice(botOrderInfo, productPriceHigherThanLowerSellPrice);
    boolean resultWhenProductPriceLowerThanUpperSellPrice =
        this.botEngineService.checkPrice(botOrderInfo, productPriceLowerThanUpperSellPrice);
    boolean resultWhenNotValidPrice =
        this.botEngineService.checkPrice(botOrderInfo, productPriceNotValid);
    // then
    assertFalse(resultWhenProductPriceHigherThanLowerSellPrice);
    assertFalse(resultWhenProductPriceLowerThanUpperSellPrice);
    assertFalse(resultWhenNotValidPrice);
  }

  @Test
  void testCheckPrice_WhenBotOrderStatusIsNotValidOpenOrClosePosition()
      throws WebClientInitializationException, InvalidBodyRequestException,
          InvalidBrokerConfigurationException {
    // given
    BotOrderInfo botOrderInfo_CLOSED =
        new BotOrderInfo(
            null, "oldOrder", "ab23423k","1.0", 12.0, 14.1, 11.1, BotOrderStatus.CLOSED, "p1", "");
    BotOrderInfo botOrderInfo_CANCELED =
        new BotOrderInfo(
            null, "oldOrder", "ab23423k","1.0", 12.0, 14.1, 11.1, BotOrderStatus.CANCELED, "p1", "");
    BotOrderInfo botOrderInfo_EXPIRED =
        new BotOrderInfo(
            null, "oldOrder", "ab23423k","1.0", 12.0, 14.1, 11.1, BotOrderStatus.EXPIRED, "p1", "");
    ProductPrice productPrice = new ProductPrice("ab23423k", "12.1");
    // when
    boolean resultForClosedOrder =
        this.botEngineService.checkPrice(botOrderInfo_CLOSED, productPrice);
    boolean resultForCanceledOrder =
        this.botEngineService.checkPrice(botOrderInfo_CANCELED, productPrice);
    boolean resultForExpiredOrder =
        this.botEngineService.checkPrice(botOrderInfo_EXPIRED, productPrice);
    // then
    assertFalse(resultForClosedOrder);
    assertFalse(resultForCanceledOrder);
    assertFalse(resultForExpiredOrder);
  }
}
