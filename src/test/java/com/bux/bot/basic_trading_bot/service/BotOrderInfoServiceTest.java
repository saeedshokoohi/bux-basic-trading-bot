package com.bux.bot.basic_trading_bot.service;

import com.bux.bot.basic_trading_bot.entity.OrderClosePosition;
import com.bux.bot.basic_trading_bot.entity.OrderOpenPosition;
import com.bux.bot.basic_trading_bot.event.global.GlobalEventBus;
import com.bux.bot.basic_trading_bot.exception.EntityValidationException;
import com.bux.bot.basic_trading_bot.exception.ValidationError;
import com.bux.bot.basic_trading_bot.entity.BotOrderInfo;
import com.bux.bot.basic_trading_bot.entity.enums.BotOrderStatus;
import com.bux.bot.basic_trading_bot.repository.BotOrderInfoRepository;
import com.bux.bot.basic_trading_bot.repository.OrderClosePositionRepository;
import com.bux.bot.basic_trading_bot.repository.OrderOpenPositionRepository;
import com.bux.bot.basic_trading_bot.service.constants.ValidationMessages;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Optional;

import static com.bux.bot.basic_trading_bot.service.constants.ValidationMessages.UPPER_SELL_PRICE_MUST_BE_HIGHER_THAN_BUY_PRICE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ContextConfiguration(classes = {BotOrderInfoService.class, GlobalEventBus.class})
@ExtendWith(SpringExtension.class)
class BotOrderInfoServiceTest {
    @MockBean
    private GlobalEventBus globalEventBus;

    @MockBean
    private BotOrderInfoRepository botOrderInfoRepository;
    @MockBean
    private OrderOpenPositionRepository orderOpenPositionRepository;
    @MockBean
    private OrderClosePositionRepository orderClosePositionRepository;


    @Autowired
    private BotOrderInfoService botOrderInfoService;

    @Test
    void testSuccessfulAddNewBotOrderInfo() throws EntityValidationException {

        // given

        BotOrderInfo botOrderInfo = new BotOrderInfo("newOrder", "ab23423k", "1.0", 12.4, 14.1, 11.1);
        BotOrderInfo botOrderInfoResult = new BotOrderInfo("newOrder", "ab23423k", "1.0", 12.4, 14.1, 11.1);
        when(this.botOrderInfoRepository.save((BotOrderInfo) any()))
                .thenReturn(botOrderInfoResult);

        // when
        Mono<BotOrderInfo> result = this.botOrderInfoService.addNewBotOrderInfo(botOrderInfo);
        //then
        verify(this.botOrderInfoRepository).save((BotOrderInfo) any());
        StepVerifier.create(result)
                .expectNextMatches(
                        entity -> {
                            return entity.getId().equals(botOrderInfoResult.getId())
                                    && entity.getStatus().equals(BotOrderStatus.ACTIVE);
                        });
    }

    @Test
    void testUpdateBotOrderInfo() throws EntityValidationException {
        //given
        Long id = 67l;
        BotOrderInfo oldOrder = new BotOrderInfo("oldOrder", "ab23423k", "1.0", 12.4, 14.1, 11.1);
        BotOrderInfo newOrder = new BotOrderInfo("newOrder", "ab23423k", "1.0", 12.4, 14.1, 11.1);
        oldOrder.setId(id);
        newOrder.setId(id);
        when(this.botOrderInfoRepository.save((BotOrderInfo) any()))
                .thenReturn(newOrder);
        when(this.botOrderInfoRepository.findById(id)).thenReturn(Optional.of(oldOrder));
        //when
        Mono<BotOrderInfo> result = this.botOrderInfoService.updateBotOrderInfo(newOrder);
        //then
        verify(this.botOrderInfoRepository).findById(id);
        verify(this.botOrderInfoRepository).save(newOrder);
        StepVerifier.create(result)
                .expectNextMatches(
                        entity -> {
                            return entity.getId().equals(id)
                                    && entity.getTitle().equals(newOrder.getTitle());
                        });
    }

    @Test
    void testUpdateBotOrderInfoWhenIdIsNotSet() throws EntityValidationException {
        //given
        Long id = 67l;
        BotOrderInfo oldOrder = new BotOrderInfo("oldOrder", "ab23423k", "1.0", 12.4, 14.1, 11.1);
        BotOrderInfo newOrder = new BotOrderInfo("newOrder", "ab23423k", "1.0", 12.4, 14.1, 11.1);
        oldOrder.setId(id);

        when(this.botOrderInfoRepository.save((BotOrderInfo) any()))
                .thenReturn(newOrder);
        when(this.botOrderInfoRepository.findById(id)).thenReturn(Optional.of(oldOrder));
        //when
        Mono<BotOrderInfo> result = this.botOrderInfoService.updateBotOrderInfo(newOrder);
        //then
        verify(this.botOrderInfoRepository, never()).findById(id);
        verify(this.botOrderInfoRepository, never()).save((BotOrderInfo) any());
        StepVerifier.create(result).expectError(EntityValidationException.class);
    }

    @Test
    void testUpdateBotOrderInfoWhenIdIsNotValid() throws EntityValidationException {
        //given

        Long invalidId = 123l;
        BotOrderInfo oldOrder = null;
        BotOrderInfo newOrder = new BotOrderInfo("newOrder", "ab23423k", "1.0", 12.4, 14.1, 11.1);
        newOrder.setId(invalidId);


        when(this.botOrderInfoRepository.save((BotOrderInfo) any()))
                .thenReturn(newOrder);
        when(this.botOrderInfoRepository.findById(invalidId)).thenReturn(Optional.empty());
        //when
        Mono<BotOrderInfo> result = this.botOrderInfoService.updateBotOrderInfo(newOrder);
        //then
        verify(this.botOrderInfoRepository).findById(invalidId);
        verify(this.botOrderInfoRepository, never()).save(any());
        StepVerifier.create(result).expectError(EntityValidationException.class);
    }

    @Test
    void testUpdateBotOrderInfoWhenGivenEntityIsNotValid() throws EntityValidationException {
        //given
        Long id = 67l;
        BotOrderInfo oldOrder = new BotOrderInfo("oldOrder", "ab23423k", "1.0", 12.4, 14.1, 11.1);
        BotOrderInfo newOrder = new BotOrderInfo("newOrder", "ab23423k", "1.0", 15.4, 14.1, 11.1);
        oldOrder.setId(id);
        newOrder.setId(id);

        when(this.botOrderInfoRepository.save((BotOrderInfo) any()))
                .thenReturn(newOrder);
        when(this.botOrderInfoRepository.findById(id)).thenReturn(Optional.of(oldOrder));
        //when
        Mono<BotOrderInfo> result = this.botOrderInfoService.updateBotOrderInfo(newOrder);
        //then
        verify(this.botOrderInfoRepository, never()).findById(id);
        verify(this.botOrderInfoRepository, never()).save((BotOrderInfo) any());
        StepVerifier.create(result).expectError(EntityValidationException.class);
        StepVerifier.create(result).expectErrorMatches(e -> {
            EntityValidationException ev = (EntityValidationException) e;
            return (ev.getErrors().size() == 1 && ev.getErrors().contains(new ValidationError("upperSellPrice", UPPER_SELL_PRICE_MUST_BE_HIGHER_THAN_BUY_PRICE)));
        });
    }

    @Test
    void testUpdateBotOrderInfoWhenStateTransitionIsNotValid() throws EntityValidationException {
        //given
        Long id = 67l;
        BotOrderInfo oldOrder = new BotOrderInfo("oldOrder", "ab23423k", "1.0", 12.4, 14.1, 11.1);
        BotOrderInfo newOrder = new BotOrderInfo("newOrder", "ab23423k", "1.0", 12.4, 14.1, 11.1);
        oldOrder.setStatus(BotOrderStatus.CLOSED);
        newOrder.setStatus(BotOrderStatus.ACTIVE);
        oldOrder.setId(id);
        newOrder.setId(id);

        when(this.botOrderInfoRepository.save((BotOrderInfo) any()))
                .thenReturn(newOrder);
        when(this.botOrderInfoRepository.findById(id)).thenReturn(Optional.of(oldOrder));
        //when
        Mono<BotOrderInfo> result = this.botOrderInfoService.updateBotOrderInfo(newOrder);
        //then
        verify(this.botOrderInfoRepository).findById(id);
        verify(this.botOrderInfoRepository, never()).save((BotOrderInfo) any());
        StepVerifier.create(result).expectError(EntityValidationException.class);
    }

    @Test
    void testValidateBotOrderInfoOnPricesValidation() throws EntityValidationException {
        // given
        BotOrderInfo botOrderInfo = new BotOrderInfo("invalid order", "ab23423k", "1.0", 12.4, 12.1, 13.1);
        // when
        EntityValidationException actualException = null;
        try {
            this.botOrderInfoService.validateBotOrderInfo(botOrderInfo);
        } catch (EntityValidationException e) {
            actualException = e;
        }
        // then
        assertThat(actualException).isNotNull();
        assertThat(actualException.getErrors()).hasSize(2);
        assertThat(actualException.getErrors())
                .anyMatch(
                        ve ->
                                ve.getField().equals("lowerSellPrice")
                                        && ValidationMessages.LOWER_SELL_PRICE_MUST_BE_LOWER_THAN_BUY_PRICE.equals(
                                        ve.getUserError()))
                .anyMatch(
                        ve ->
                                ve.getField().equals("upperSellPrice")
                                        && UPPER_SELL_PRICE_MUST_BE_HIGHER_THAN_BUY_PRICE.equals(
                                        ve.getUserError()));
    }

    @Test
    void testValidateBotOrderInfoOnNullBuyPrice() throws EntityValidationException {
        // given
        BotOrderInfo botOrderInfoNullBuyPrice =
                new BotOrderInfo("invalid order", "ab23423k", "1.0", null, 12.1, 13.1);
        // when
        EntityValidationException actualException = null;
        try {
            this.botOrderInfoService.validateBotOrderInfo(botOrderInfoNullBuyPrice);
        } catch (EntityValidationException e) {
            actualException = e;
        }
        // then
        assertThat(actualException).isNotNull();
        assertThat(actualException.getErrors()).hasSize(1);
        assertThat(actualException.getErrors())
                .anyMatch(
                        ve ->
                                ve.getField().equals("buyPrice")
                                        && ValidationMessages.NULL_VALUE_IS_NOT_VALID.equals(
                                        ve.getUserError()))
        ;
    }

    @Test
    void testValidateBotOrderInfoOnNullUpperSellPrice() throws EntityValidationException {
        // given
        BotOrderInfo botOrderInfoNullBuyPrice =
                new BotOrderInfo("invalid order", "ab23423k", "1.0", 12.3, null, 13.1);
        // when
        EntityValidationException actualException = null;
        try {
            this.botOrderInfoService.validateBotOrderInfo(botOrderInfoNullBuyPrice);
        } catch (EntityValidationException e) {
            actualException = e;
        }
        // then
        assertThat(actualException).isNotNull();
        assertThat(actualException.getErrors()).hasSize(1);
        assertThat(actualException.getErrors())
                .anyMatch(
                        ve ->
                                ve.getField().equals("upperSellPrice")
                                        && ValidationMessages.NULL_VALUE_IS_NOT_VALID.equals(
                                        ve.getUserError()))
        ;
    }

    @Test
    void testValidateBotOrderInfoOnNullLowerSellPrice() throws EntityValidationException {
        // given
        BotOrderInfo botOrderInfoNullBuyPrice =
                new BotOrderInfo("invalid order", "ab23423k", "1.0", 12.3, 13.5, null);
        // when
        EntityValidationException actualException = null;
        try {
            this.botOrderInfoService.validateBotOrderInfo(botOrderInfoNullBuyPrice);
        } catch (EntityValidationException e) {
            actualException = e;
        }
        // then
        assertThat(actualException).isNotNull();
        assertThat(actualException.getErrors()).hasSize(1);
        assertThat(actualException.getErrors())
                .anyMatch(
                        ve ->
                                ve.getField().equals("lowerSellPrice")
                                        && ValidationMessages.NULL_VALUE_IS_NOT_VALID.equals(
                                        ve.getUserError()))
        ;
    }

    @Test
    void testValidateBotOrderInfoStateTransition() throws EntityValidationException {
        // TODO: This test is incomplete.
        //   Reason: No meaningful assertions found.
        //   Diffblue Cover was unable to create an assertion.
        //   Make sure that fields modified by validateBotOrderInfoStateTransition(BotOrderInfo, BotOrderInfo)
        //   have package-private, protected, or public getters.
        //   See https://diff.blue/R004 to resolve this issue.

        OrderClosePosition orderClosePosition = new OrderClosePosition();
        orderClosePosition.setProductSecurityId("42");
        orderClosePosition.setDateCreated(1L);
        orderClosePosition.setProductSymbol("Product Symbol");
        orderClosePosition.setPriceCurrency("GBP");
        orderClosePosition.setPositionId("42");
        orderClosePosition.setInvestingAmountDecimals(1);
        orderClosePosition.setClientId("42");
        orderClosePosition.setLeverage(1);
        orderClosePosition.setProfitAndLossAmount("10");
        orderClosePosition.setInvestingAmountCurrency("GBP");
        orderClosePosition.setProductDisplayName("Product Display Name");
        orderClosePosition.setPriceAmount("10");
        orderClosePosition.setPriceDecimals(1);
        orderClosePosition.setId(123L);
        orderClosePosition.setProfitAndLossDecimals(1);
        orderClosePosition.setInvestingAmount("10");
        orderClosePosition.setDirection("Direction");
        orderClosePosition.setProfitAndLossCurrency("GBP");
        orderClosePosition.setType("Type");
        orderClosePosition.setOrderId(123L);

        OrderOpenPosition orderOpenPosition = new OrderOpenPosition();
        orderOpenPosition.setProductSecurityId("42");
        orderOpenPosition.setDateCreated(1L);
        orderOpenPosition.setProductSymbol("Product Symbol");
        orderOpenPosition.setPriceCurrency("GBP");
        orderOpenPosition.setPositionId("42");
        orderOpenPosition.setInvestingAmountDecimals(1);
        orderOpenPosition.setClientId("42");
        orderOpenPosition.setLeverage(1);
        orderOpenPosition.setInvestingAmountCurrency("GBP");
        orderOpenPosition.setProductDisplayName("Product Display Name");
        orderOpenPosition.setPriceAmount("10");
        orderOpenPosition.setPriceDecimals(1);
        orderOpenPosition.setId(123L);
        orderOpenPosition.setInvestingAmount("10");
        orderOpenPosition.setDirection("Direction");
        orderOpenPosition.setType("Type");
        orderOpenPosition.setOrderId(123L);

        BotOrderInfo botOrderInfo = new BotOrderInfo();
        botOrderInfo.setDecimals(1);
        botOrderInfo.setBuyPrice(10.0);
        botOrderInfo.setPositionId("42");
        botOrderInfo.setAmount("10");
        botOrderInfo.setUpperSellPrice(10.0);
        botOrderInfo.setCurrency("GBP");
        botOrderInfo.setLowerSellPrice(10.0);
        botOrderInfo.setDescription("The characteristics of someone or something");
        botOrderInfo.setProductId("42");
        botOrderInfo.setLeverage(1);
        botOrderInfo.setStatus(BotOrderStatus.ACTIVE);
        botOrderInfo.setId(123L);
        botOrderInfo.setTitle("Dr");
        botOrderInfo.setClosePosition(orderClosePosition);
        botOrderInfo.setOpenPosition(orderOpenPosition);

        OrderClosePosition orderClosePosition1 = new OrderClosePosition();
        orderClosePosition1.setProductSecurityId("42");
        orderClosePosition1.setDateCreated(1L);
        orderClosePosition1.setProductSymbol("Product Symbol");
        orderClosePosition1.setPriceCurrency("GBP");
        orderClosePosition1.setPositionId("42");
        orderClosePosition1.setInvestingAmountDecimals(1);
        orderClosePosition1.setClientId("42");
        orderClosePosition1.setLeverage(1);
        orderClosePosition1.setProfitAndLossAmount("10");
        orderClosePosition1.setInvestingAmountCurrency("GBP");
        orderClosePosition1.setProductDisplayName("Product Display Name");
        orderClosePosition1.setPriceAmount("10");
        orderClosePosition1.setPriceDecimals(1);
        orderClosePosition1.setId(123L);
        orderClosePosition1.setProfitAndLossDecimals(1);
        orderClosePosition1.setInvestingAmount("10");
        orderClosePosition1.setDirection("Direction");
        orderClosePosition1.setProfitAndLossCurrency("GBP");
        orderClosePosition1.setType("Type");
        orderClosePosition1.setOrderId(123L);

        OrderOpenPosition orderOpenPosition1 = new OrderOpenPosition();
        orderOpenPosition1.setProductSecurityId("42");
        orderOpenPosition1.setDateCreated(1L);
        orderOpenPosition1.setProductSymbol("Product Symbol");
        orderOpenPosition1.setPriceCurrency("GBP");
        orderOpenPosition1.setPositionId("42");
        orderOpenPosition1.setInvestingAmountDecimals(1);
        orderOpenPosition1.setClientId("42");
        orderOpenPosition1.setLeverage(1);
        orderOpenPosition1.setInvestingAmountCurrency("GBP");
        orderOpenPosition1.setProductDisplayName("Product Display Name");
        orderOpenPosition1.setPriceAmount("10");
        orderOpenPosition1.setPriceDecimals(1);
        orderOpenPosition1.setId(123L);
        orderOpenPosition1.setInvestingAmount("10");
        orderOpenPosition1.setDirection("Direction");
        orderOpenPosition1.setType("Type");
        orderOpenPosition1.setOrderId(123L);

        BotOrderInfo botOrderInfo1 = new BotOrderInfo();
        botOrderInfo1.setDecimals(1);
        botOrderInfo1.setBuyPrice(10.0);
        botOrderInfo1.setPositionId("42");
        botOrderInfo1.setAmount("10");
        botOrderInfo1.setUpperSellPrice(10.0);
        botOrderInfo1.setCurrency("GBP");
        botOrderInfo1.setLowerSellPrice(10.0);
        botOrderInfo1.setDescription("The characteristics of someone or something");
        botOrderInfo1.setProductId("42");
        botOrderInfo1.setLeverage(1);
        botOrderInfo1.setStatus(BotOrderStatus.ACTIVE);
        botOrderInfo1.setId(123L);
        botOrderInfo1.setTitle("Dr");
        botOrderInfo1.setClosePosition(orderClosePosition1);
        botOrderInfo1.setOpenPosition(orderOpenPosition1);
        this.botOrderInfoService.validateBotOrderInfoStateTransition(botOrderInfo, botOrderInfo1);
    }

    @Test
    void testValidateBotOrderInfoStateTransition2() throws EntityValidationException {
        OrderClosePosition orderClosePosition = new OrderClosePosition();
        orderClosePosition.setProductSecurityId("42");
        orderClosePosition.setDateCreated(1L);
        orderClosePosition.setProductSymbol("Product Symbol");
        orderClosePosition.setPriceCurrency("GBP");
        orderClosePosition.setPositionId("42");
        orderClosePosition.setInvestingAmountDecimals(1);
        orderClosePosition.setClientId("42");
        orderClosePosition.setLeverage(1);
        orderClosePosition.setProfitAndLossAmount("10");
        orderClosePosition.setInvestingAmountCurrency("GBP");
        orderClosePosition.setProductDisplayName("Product Display Name");
        orderClosePosition.setPriceAmount("10");
        orderClosePosition.setPriceDecimals(1);
        orderClosePosition.setId(123L);
        orderClosePosition.setProfitAndLossDecimals(1);
        orderClosePosition.setInvestingAmount("10");
        orderClosePosition.setDirection("Direction");
        orderClosePosition.setProfitAndLossCurrency("GBP");
        orderClosePosition.setType("Type");
        orderClosePosition.setOrderId(123L);

        OrderOpenPosition orderOpenPosition = new OrderOpenPosition();
        orderOpenPosition.setProductSecurityId("42");
        orderOpenPosition.setDateCreated(1L);
        orderOpenPosition.setProductSymbol("Product Symbol");
        orderOpenPosition.setPriceCurrency("GBP");
        orderOpenPosition.setPositionId("42");
        orderOpenPosition.setInvestingAmountDecimals(1);
        orderOpenPosition.setClientId("42");
        orderOpenPosition.setLeverage(1);
        orderOpenPosition.setInvestingAmountCurrency("GBP");
        orderOpenPosition.setProductDisplayName("Product Display Name");
        orderOpenPosition.setPriceAmount("10");
        orderOpenPosition.setPriceDecimals(1);
        orderOpenPosition.setId(123L);
        orderOpenPosition.setInvestingAmount("10");
        orderOpenPosition.setDirection("Direction");
        orderOpenPosition.setType("Type");
        orderOpenPosition.setOrderId(123L);

        BotOrderInfo botOrderInfo = new BotOrderInfo();
        botOrderInfo.setDecimals(1);
        botOrderInfo.setBuyPrice(10.0);
        botOrderInfo.setPositionId("42");
        botOrderInfo.setAmount("10");
        botOrderInfo.setUpperSellPrice(10.0);
        botOrderInfo.setCurrency("GBP");
        botOrderInfo.setLowerSellPrice(10.0);
        botOrderInfo.setDescription("The characteristics of someone or something");
        botOrderInfo.setProductId("42");
        botOrderInfo.setLeverage(1);
        botOrderInfo.setStatus(BotOrderStatus.OPEN);
        botOrderInfo.setId(123L);
        botOrderInfo.setTitle("Dr");
        botOrderInfo.setClosePosition(orderClosePosition);
        botOrderInfo.setOpenPosition(orderOpenPosition);

        OrderClosePosition orderClosePosition1 = new OrderClosePosition();
        orderClosePosition1.setProductSecurityId("42");
        orderClosePosition1.setDateCreated(1L);
        orderClosePosition1.setProductSymbol("Product Symbol");
        orderClosePosition1.setPriceCurrency("GBP");
        orderClosePosition1.setPositionId("42");
        orderClosePosition1.setInvestingAmountDecimals(1);
        orderClosePosition1.setClientId("42");
        orderClosePosition1.setLeverage(1);
        orderClosePosition1.setProfitAndLossAmount("10");
        orderClosePosition1.setInvestingAmountCurrency("GBP");
        orderClosePosition1.setProductDisplayName("Product Display Name");
        orderClosePosition1.setPriceAmount("10");
        orderClosePosition1.setPriceDecimals(1);
        orderClosePosition1.setId(123L);
        orderClosePosition1.setProfitAndLossDecimals(1);
        orderClosePosition1.setInvestingAmount("10");
        orderClosePosition1.setDirection("Direction");
        orderClosePosition1.setProfitAndLossCurrency("GBP");
        orderClosePosition1.setType("Type");
        orderClosePosition1.setOrderId(123L);

        OrderOpenPosition orderOpenPosition1 = new OrderOpenPosition();
        orderOpenPosition1.setProductSecurityId("42");
        orderOpenPosition1.setDateCreated(1L);
        orderOpenPosition1.setProductSymbol("Product Symbol");
        orderOpenPosition1.setPriceCurrency("GBP");
        orderOpenPosition1.setPositionId("42");
        orderOpenPosition1.setInvestingAmountDecimals(1);
        orderOpenPosition1.setClientId("42");
        orderOpenPosition1.setLeverage(1);
        orderOpenPosition1.setInvestingAmountCurrency("GBP");
        orderOpenPosition1.setProductDisplayName("Product Display Name");
        orderOpenPosition1.setPriceAmount("10");
        orderOpenPosition1.setPriceDecimals(1);
        orderOpenPosition1.setId(123L);
        orderOpenPosition1.setInvestingAmount("10");
        orderOpenPosition1.setDirection("Direction");
        orderOpenPosition1.setType("Type");
        orderOpenPosition1.setOrderId(123L);

        BotOrderInfo botOrderInfo1 = new BotOrderInfo();
        botOrderInfo1.setDecimals(1);
        botOrderInfo1.setBuyPrice(10.0);
        botOrderInfo1.setPositionId("42");
        botOrderInfo1.setAmount("10");
        botOrderInfo1.setUpperSellPrice(10.0);
        botOrderInfo1.setCurrency("GBP");
        botOrderInfo1.setLowerSellPrice(10.0);
        botOrderInfo1.setDescription("The characteristics of someone or something");
        botOrderInfo1.setProductId("42");
        botOrderInfo1.setLeverage(1);
        botOrderInfo1.setStatus(BotOrderStatus.ACTIVE);
        botOrderInfo1.setId(123L);
        botOrderInfo1.setTitle("Dr");
        botOrderInfo1.setClosePosition(orderClosePosition1);
        botOrderInfo1.setOpenPosition(orderOpenPosition1);
        assertThrows(EntityValidationException.class,
                () -> this.botOrderInfoService.validateBotOrderInfoStateTransition(botOrderInfo, botOrderInfo1));
    }

    @Test
    void testValidateBotOrderInfoStateTransition_CLOSE_to_ACTIVE() throws EntityValidationException {
        //given
        BotOrderInfo fromState = new BotOrderInfo("oldOrder", "ab23423k", "1.0", 12.4, 14.1, 11.1);
        BotOrderInfo toState = new BotOrderInfo("newOrder", "ab23423k", "1.0", 12.4, 14.1, 11.1);
        fromState.setStatus(BotOrderStatus.CLOSED);
        toState.setStatus(BotOrderStatus.ACTIVE);
        EntityValidationException actualException = null;

        //when
        try {
            this.botOrderInfoService.validateBotOrderInfoStateTransition(fromState, toState);
        } catch (EntityValidationException e) {
            actualException = e;
        }
        //then
        assertThat(actualException).isNotNull();
        assertThat(actualException).hasMessage(ValidationMessages.ACTIVE_STATE_IS_INIT_STATE);
    }

    @Test
    void testValidateBotOrderInfoStateTransition_CLOSE_to_OPEN() throws EntityValidationException {
        //given
        BotOrderInfo fromState = new BotOrderInfo("oldOrder", "ab23423k", "1.0", 12.4, 14.1, 11.1);
        BotOrderInfo toState = new BotOrderInfo("newOrder", "ab23423k", "1.0", 12.4, 14.1, 11.1);
        fromState.setStatus(BotOrderStatus.CLOSED);
        toState.setStatus(BotOrderStatus.OPEN);
        EntityValidationException actualException = null;

        //when
        try {
            this.botOrderInfoService.validateBotOrderInfoStateTransition(fromState, toState);
        } catch (EntityValidationException e) {
            actualException = e;
        }
        //then
        assertThat(actualException).isNotNull();
        assertThat(actualException).hasMessage(ValidationMessages.ONLY_ACTIVE_ORDER_CAN_CHANGE_TO_OPEN_STATE);
    }

    @Test
    void testValidateBotOrderInfoStateTransition_OPEN_to_ACTIVE() throws EntityValidationException {
        //given
        BotOrderInfo fromState = new BotOrderInfo("oldOrder", "ab23423k", "1.0", 12.4, 14.1, 11.1);
        BotOrderInfo toState = new BotOrderInfo("newOrder", "ab23423k", "1.0", 12.4, 14.1, 11.1);
        fromState.setStatus(BotOrderStatus.OPEN);
        toState.setStatus(BotOrderStatus.ACTIVE);
        EntityValidationException actualException = null;

        //when
        try {
            this.botOrderInfoService.validateBotOrderInfoStateTransition(fromState, toState);
        } catch (EntityValidationException e) {
            actualException = e;
        }
        //then
        assertThat(actualException).isNotNull();
        assertThat(actualException).hasMessage(ValidationMessages.ACTIVE_STATE_IS_INIT_STATE);
    }
}
