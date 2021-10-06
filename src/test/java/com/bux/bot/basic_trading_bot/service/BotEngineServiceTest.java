package com.bux.bot.basic_trading_bot.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.bux.bot.basic_trading_bot.client.rest.TradeClientService;
import com.bux.bot.basic_trading_bot.client.rest.bux_impl.BuxTradeService;
import com.bux.bot.basic_trading_bot.client.rest.bux_impl.BuxWebClientFactory;
import com.bux.bot.basic_trading_bot.config.BrokersConfiguration;
import com.bux.bot.basic_trading_bot.dto.ProductPrice;
import com.bux.bot.basic_trading_bot.exception.InvalidBrokerConfigurationException;
import com.bux.bot.basic_trading_bot.model.BotOrderInfo;
import com.bux.bot.basic_trading_bot.model.enums.BotOrderStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ContextConfiguration(classes = {BotEngineService.class})
@ExtendWith(SpringExtension.class)
class BotEngineServiceTest {
    @Autowired
    private BotEngineService botEngineService;

    @MockBean
    private TradeClientService tradeClientService;

    @Test
    void testConstructor() throws InvalidBrokerConfigurationException {
        BuxTradeService buxTradeService = new BuxTradeService(new BuxWebClientFactory(new BrokersConfiguration()));
        TradeClientService tradeClientService = (new BotEngineService(buxTradeService)).tradeClientService;
        assertTrue(tradeClientService instanceof BuxTradeService);
        assertSame(tradeClientService, buxTradeService);
    }

    @Test
    void testCheckPrice_WhenActiveBotOrderLeadsToOpenPosition() {
        //given
        BotOrderInfo botOrderInfo = new BotOrderInfo(null,"oldOrder", "ab23423k", 12.4, 14.1, 11.1, BotOrderStatus.ACTIVE,"p1","");
        ProductPrice productPriceLowerThanBuyPrice=new ProductPrice("ab23423k","12.1");
        ProductPrice productPriceEqualToBuyPrice=new ProductPrice("ab23423k","12.4");
        //when
        boolean resultWhenLowerThanBuyPrice = this.botEngineService.checkPrice(botOrderInfo, productPriceLowerThanBuyPrice);
        boolean resultWhenLowerEqualToBuyPrice = this.botEngineService.checkPrice(botOrderInfo, productPriceEqualToBuyPrice);
        //then
        assertTrue(resultWhenLowerThanBuyPrice);
        assertTrue(resultWhenLowerEqualToBuyPrice);
    }
    @Test
    void testCheckPrice_WhenActiveBotOrderNotleadsToOpenPosition() {
        //given
        BotOrderInfo botOrderInfo = new BotOrderInfo(null,"oldOrder", "ab23423k", 12.0, 14.1, 11.1, BotOrderStatus.ACTIVE,"p1","");
        ProductPrice productPriceHigherThanBuyPrice=new ProductPrice("ab23423k","12.1");
        ProductPrice productPriceNotValid=new ProductPrice("ab23423k","notValid");
        //when
        boolean resultWhenLowerThanBuyPrice = this.botEngineService.checkPrice(botOrderInfo, productPriceHigherThanBuyPrice);
        boolean resultWhenNotValidPrice = this.botEngineService.checkPrice(botOrderInfo, productPriceNotValid);
        //then
        assertFalse(resultWhenLowerThanBuyPrice);
        assertFalse(resultWhenNotValidPrice);
    }
    @Test
    void testCheckPrice_WhenOpenBotOrderLeadsToClosePosition() {
        //given
        BotOrderInfo botOrderInfo = new BotOrderInfo(null,"oldOrder", "ab23423k", 12.4, 14.1, 11.1, BotOrderStatus.OPEN,"p1","");
        ProductPrice productPriceLowerThanLowerSellPrice=new ProductPrice("ab23423k","11.0");
        ProductPrice productPriceHigherThanUpperSellPrice=new ProductPrice("ab23423k","14.4");
        //when
        boolean resultWhenProductPriceLowerThanLowerSellPrice = this.botEngineService.checkPrice(botOrderInfo, productPriceLowerThanLowerSellPrice);
        boolean resultWhenProductPriceHigherThanUpperSellPrice = this.botEngineService.checkPrice(botOrderInfo, productPriceHigherThanUpperSellPrice);
        //then
        assertTrue(resultWhenProductPriceLowerThanLowerSellPrice);
        assertTrue(resultWhenProductPriceHigherThanUpperSellPrice);
    }
    @Test
    void testCheckPrice_WhenOpenBotOrderNotleadsToOpenPosition() {
        //given
        BotOrderInfo botOrderInfo = new BotOrderInfo(null,"oldOrder", "ab23423k", 12.0, 14.1, 11.1, BotOrderStatus.OPEN,"p1","");
        ProductPrice productPriceHigherThanLowerSellPrice=new ProductPrice("ab23423k","11.2");
        ProductPrice productPriceLowerThanUpperSellPrice=new ProductPrice("ab23423k","14.0");
        ProductPrice productPriceNotValid=new ProductPrice("ab23423k","notValidPrice");
        //when
        boolean resultWhenProductPriceHigherThanLowerSellPrice = this.botEngineService.checkPrice(botOrderInfo, productPriceHigherThanLowerSellPrice);
        boolean resultWhenProductPriceLowerThanUpperSellPrice = this.botEngineService.checkPrice(botOrderInfo, productPriceLowerThanUpperSellPrice);
        boolean resultWhenNotValidPrice = this.botEngineService.checkPrice(botOrderInfo, productPriceNotValid);
        //then
        assertFalse(resultWhenProductPriceHigherThanLowerSellPrice);
        assertFalse(resultWhenProductPriceLowerThanUpperSellPrice);
        assertFalse(resultWhenNotValidPrice);
    }
    @Test
    void testCheckPrice_WhenBotOrderStatusIsNotValidOpenOrClosePosition() {
        //given
        BotOrderInfo botOrderInfo_CLOSED = new BotOrderInfo(null,"oldOrder", "ab23423k", 12.0, 14.1, 11.1, BotOrderStatus.CLOSED,"p1","");
        BotOrderInfo botOrderInfo_CANCELED = new BotOrderInfo(null,"oldOrder", "ab23423k", 12.0, 14.1, 11.1, BotOrderStatus.CANCELED,"p1","");
        BotOrderInfo botOrderInfo_EXPIRED = new BotOrderInfo(null,"oldOrder", "ab23423k", 12.0, 14.1, 11.1, BotOrderStatus.EXPIRED,"p1","");
        ProductPrice productPrice=new ProductPrice("ab23423k","12.1");
        //when
        boolean resultForClosedOrder = this.botEngineService.checkPrice(botOrderInfo_CLOSED, productPrice);
        boolean resultForCanceledOrder = this.botEngineService.checkPrice(botOrderInfo_CANCELED, productPrice);
        boolean resultForExpiredOrder = this.botEngineService.checkPrice(botOrderInfo_EXPIRED, productPrice);
        //then
        assertFalse(resultForClosedOrder);
        assertFalse(resultForCanceledOrder);
        assertFalse(resultForExpiredOrder);
    }

}

