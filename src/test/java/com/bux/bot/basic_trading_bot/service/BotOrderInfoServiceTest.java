package com.bux.bot.basic_trading_bot.service;

import com.bux.bot.basic_trading_bot.exception.EntityValidationException;
import com.bux.bot.basic_trading_bot.exception.ValidationError;
import com.bux.bot.basic_trading_bot.model.BotOrderInfo;
import com.bux.bot.basic_trading_bot.model.enums.BotOrderStatus;
import com.bux.bot.basic_trading_bot.repository.BotOrderInfoRepository;
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

@ContextConfiguration(classes = {BotOrderInfoService.class})
@ExtendWith(SpringExtension.class)
class BotOrderInfoServiceTest {
  @MockBean private BotOrderInfoRepository botOrderInfoRepository;

  @Autowired private BotOrderInfoService botOrderInfoService;

  @Test
  void testSuccessfulAddNewBotOrderInfo() throws EntityValidationException {

    // given

    BotOrderInfo botOrderInfo = new BotOrderInfo("newOrder", "ab23423k", 12.4, 14.1, 11.1);
    BotOrderInfo botOrderInfoResult = new BotOrderInfo("newOrder", "ab23423k", 12.4, 14.1, 11.1);
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
      Long id=67l;
      BotOrderInfo oldOrder = new BotOrderInfo("oldOrder", "ab23423k", 12.4, 14.1, 11.1);
      BotOrderInfo newOrder = new BotOrderInfo("newOrder", "ab23423k", 12.4, 14.1, 11.1);
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
        Long id=67l;
        BotOrderInfo oldOrder = new BotOrderInfo("oldOrder", "ab23423k", 12.4, 14.1, 11.1);
        BotOrderInfo newOrder = new BotOrderInfo("newOrder", "ab23423k", 12.4, 14.1, 11.1);
        oldOrder.setId(id);

        when(this.botOrderInfoRepository.save((BotOrderInfo) any()))
                .thenReturn(newOrder);
        when(this.botOrderInfoRepository.findById(id)).thenReturn(Optional.of(oldOrder));
        //when
        Mono<BotOrderInfo> result = this.botOrderInfoService.updateBotOrderInfo(newOrder);
        //then
        verify(this.botOrderInfoRepository,never()).findById(id);
        verify(this.botOrderInfoRepository,never()).save((BotOrderInfo) any());
        StepVerifier.create(result).expectError(EntityValidationException.class);
    }
    @Test
    void testUpdateBotOrderInfoWhenIdIsNotValid() throws EntityValidationException {
        //given

        Long invalidId=123l;
        BotOrderInfo oldOrder = null;
        BotOrderInfo newOrder = new BotOrderInfo("newOrder", "ab23423k", 12.4, 14.1, 11.1);
        newOrder.setId(invalidId);


        when(this.botOrderInfoRepository.save((BotOrderInfo) any()))
                .thenReturn(newOrder);
        when(this.botOrderInfoRepository.findById(invalidId)).thenReturn(Optional.empty());
        //when
        Mono<BotOrderInfo> result = this.botOrderInfoService.updateBotOrderInfo(newOrder);
        //then
        verify(this.botOrderInfoRepository).findById(invalidId);
        verify(this.botOrderInfoRepository,never()).save(any());
        StepVerifier.create(result).expectError(EntityValidationException.class);
    }
    @Test
    void testUpdateBotOrderInfoWhenGivenEntityIsNotValid() throws EntityValidationException {
        //given
        Long id=67l;
        BotOrderInfo oldOrder = new BotOrderInfo("oldOrder", "ab23423k", 12.4, 14.1, 11.1);
        BotOrderInfo newOrder = new BotOrderInfo("newOrder", "ab23423k", 15.4, 14.1, 11.1);
        oldOrder.setId(id);
        newOrder.setId(id);

        when(this.botOrderInfoRepository.save((BotOrderInfo) any()))
                .thenReturn(newOrder);
        when(this.botOrderInfoRepository.findById(id)).thenReturn(Optional.of(oldOrder));
        //when
        Mono<BotOrderInfo> result = this.botOrderInfoService.updateBotOrderInfo(newOrder);
        //then
        verify(this.botOrderInfoRepository,never()).findById(id);
        verify(this.botOrderInfoRepository,never()).save((BotOrderInfo) any());
        StepVerifier.create(result).expectError(EntityValidationException.class);
        StepVerifier.create(result).expectErrorMatches(e->{
            EntityValidationException ev=(EntityValidationException)e;
            return (ev.getErrors().size()==1 && ev.getErrors().contains(new ValidationError("upperSellPrice",UPPER_SELL_PRICE_MUST_BE_HIGHER_THAN_BUY_PRICE)));
        });
    }
    @Test
    void testUpdateBotOrderInfoWhenStateTransitionIsNotValid() throws EntityValidationException {
        //given
        Long id=67l;
        BotOrderInfo oldOrder = new BotOrderInfo("oldOrder", "ab23423k", 12.4, 14.1, 11.1);
        BotOrderInfo newOrder = new BotOrderInfo("newOrder", "ab23423k", 12.4, 14.1, 11.1);
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
        verify(this.botOrderInfoRepository,never()).save((BotOrderInfo) any());
        StepVerifier.create(result).expectError(EntityValidationException.class);
    }
  @Test
  void testValidateBotOrderInfoOnPricesValidation() throws EntityValidationException {
    // given
    BotOrderInfo botOrderInfo = new BotOrderInfo("invalid order", "ab23423k", 12.4, 12.1, 13.1);
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
        new BotOrderInfo("invalid order", "ab23423k", null, 12.1, 13.1);
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
                new BotOrderInfo("invalid order", "ab23423k", 12.3, null, 13.1);
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
                new BotOrderInfo("invalid order", "ab23423k", 12.3, 13.5, null);
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
  void testValidateBotOrderInfoStateTransition_CLOSE_to_ACTIVE() throws EntityValidationException {
   //given
      BotOrderInfo fromState = new BotOrderInfo("oldOrder", "ab23423k", 12.4, 14.1, 11.1);
      BotOrderInfo toState = new BotOrderInfo("newOrder", "ab23423k", 12.4, 14.1, 11.1);
      fromState.setStatus(BotOrderStatus.CLOSED);
      toState.setStatus(BotOrderStatus.ACTIVE);
      EntityValidationException actualException = null;

      //when
      try {
          this.botOrderInfoService.validateBotOrderInfoStateTransition(fromState,toState);
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
        BotOrderInfo fromState = new BotOrderInfo("oldOrder", "ab23423k", 12.4, 14.1, 11.1);
        BotOrderInfo toState = new BotOrderInfo("newOrder", "ab23423k", 12.4, 14.1, 11.1);
        fromState.setStatus(BotOrderStatus.CLOSED);
        toState.setStatus(BotOrderStatus.OPEN);
        EntityValidationException actualException = null;

        //when
        try {
            this.botOrderInfoService.validateBotOrderInfoStateTransition(fromState,toState);
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
        BotOrderInfo fromState = new BotOrderInfo("oldOrder", "ab23423k", 12.4, 14.1, 11.1);
        BotOrderInfo toState = new BotOrderInfo("newOrder", "ab23423k", 12.4, 14.1, 11.1);
        fromState.setStatus(BotOrderStatus.OPEN);
        toState.setStatus(BotOrderStatus.ACTIVE);
        EntityValidationException actualException = null;

        //when
        try {
            this.botOrderInfoService.validateBotOrderInfoStateTransition(fromState,toState);
        } catch (EntityValidationException e) {
            actualException = e;
        }
        //then
        assertThat(actualException).isNotNull();
        assertThat(actualException).hasMessage(ValidationMessages.ACTIVE_STATE_IS_INIT_STATE);
    }
}
