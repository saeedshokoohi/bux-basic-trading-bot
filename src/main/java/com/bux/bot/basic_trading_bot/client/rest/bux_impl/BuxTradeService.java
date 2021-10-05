package com.bux.bot.basic_trading_bot.client.rest.bux_impl;

import com.bux.bot.basic_trading_bot.client.rest.TradeClientService;
import com.bux.bot.basic_trading_bot.dto.*;
import com.bux.bot.basic_trading_bot.dto.enums.PositionDirection;
import com.bux.bot.basic_trading_bot.exception.InvalidBodyRequestException;
import com.bux.bot.basic_trading_bot.exception.InvalidBrokerConfigurationException;
import com.bux.bot.basic_trading_bot.exception.WebClientApiCallException;
import com.bux.bot.basic_trading_bot.exception.WebClientInitializationException;
import com.bux.bot.basic_trading_bot.util.JsonUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Service
public class BuxTradeService implements TradeClientService {
  // injected fields
  final BuxWebClientFactory buxWebClientFactory;

  // constructors
  public BuxTradeService(BuxWebClientFactory buxWebClientFactory) {
    this.buxWebClientFactory = buxWebClientFactory;
  }
  // public methods

  /***
   * service for opening new long position
   * @param productId
   * @param amount
   * @param leverage
   * @param decimals
   * @param currency
   * @return
   * @throws WebClientInitializationException
   * @throws InvalidBrokerConfigurationException
   */
  public Mono<OpenPositionResponse> openLongPosition(
      @NotNull String productId,
      @NotNull String amount,
      int leverage,
      int decimals,
      String currency)
      throws WebClientInitializationException, InvalidBrokerConfigurationException,
          InvalidBodyRequestException {
    if (buxWebClientFactory == null)
      throw new WebClientInitializationException("buxWebClient could not be initialized");
    OpenPositionRequest openPositionRequest =
        createOpenPositionRequest(
            PositionDirection.BUY, productId, amount, leverage, decimals, currency);
    validateOpenPositionRequest(openPositionRequest);
    String uri = "/users/me/trades";
    return buxWebClientFactory
        .getWebClient()
        .post()
        .uri(uri)
        .body(Mono.just(openPositionRequest), OpenPositionRequest.class)
        .retrieve()
        .onStatus(HttpStatus::isError, this::handleError)
        .bodyToMono(OpenPositionResponse.class);
  }

  public Mono<ClosePositionResponse> closePosition(@NotNull String positionId)
      throws WebClientInitializationException, InvalidBrokerConfigurationException {
    if (buxWebClientFactory == null)
      throw new WebClientInitializationException("buxWebClient could not be initialized");
    String uri = "/users/me/trades/" + positionId;
    return buxWebClientFactory
        .getWebClient()
        .delete()
        .uri(uri)
        .retrieve()
        .onStatus(HttpStatus::isError, this::handleError)
        .bodyToMono(ClosePositionResponse.class);
  }
  /***
   * validating validateOpenPositionRequest and return InvalidBodyRequestException with related error
   * @param openPositionRequest
   * @throws InvalidBodyRequestException
   */
  public void validateOpenPositionRequest(OpenPositionRequest openPositionRequest)
      throws InvalidBodyRequestException {
    if (openPositionRequest.getLeverage() <= 0)
      throw new InvalidBodyRequestException("leverage", " leverage is grater than Zero.");
    if (openPositionRequest.getInvestingAmount() == null)
      throw new InvalidBodyRequestException("InvestingAmount", "InvestingAmount is not set.");
    BigDecimal amount = new BigDecimal(openPositionRequest.getInvestingAmount().getAmount());
    if (amount.compareTo(BigDecimal.ZERO) < 1)
      throw new InvalidBodyRequestException(
          "InvestingAmount.amount", "InvestingAmount.amount is grater than Zero.");
  }
  // private methods

  /***
   * handling webclient errors
   * @param response
   * @return
   */
  private Mono<? extends Throwable> handleError(ClientResponse response) {
    String errorString = response.bodyToMono(String.class).block();
    if (response.statusCode().is5xxServerError() || response.statusCode().is4xxClientError()) {
      ErrorResponse responseBody = response.bodyToMono(ErrorResponse.class).block();
      try {
        errorString = JsonUtil.toJsonFormat(responseBody);
      } catch (JsonProcessingException e) {
        e.printStackTrace();
      }
    }
    return Mono.error(new WebClientApiCallException(errorString));
  }

  /**
   * create OpenPositionRequest based on given parameters
   *
   * @param direction
   * @param productId
   * @param amount
   * @param leverage
   * @param decimals
   * @param currency
   * @return
   */
  private OpenPositionRequest createOpenPositionRequest(
      PositionDirection direction,
      String productId,
      String amount,
      int leverage,
      int decimals,
      String currency) {

    OpenPositionRequest openPositionRequest = new OpenPositionRequest();
    openPositionRequest.setProductId(productId);
    openPositionRequest.setDirection(direction.getName());
    openPositionRequest.setLeverage(leverage);
    InvestingAmount investingAmount = new InvestingAmount(currency, decimals, amount);
    openPositionRequest.setInvestingAmount(investingAmount);
    return openPositionRequest;
  }
}
