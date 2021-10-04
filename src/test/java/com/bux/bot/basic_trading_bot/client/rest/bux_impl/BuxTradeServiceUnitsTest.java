package com.bux.bot.basic_trading_bot.client.rest.bux_impl;

import com.bux.bot.basic_trading_bot.client.rest.bux_impl.dto.*;
import com.bux.bot.basic_trading_bot.config.BrokersConfiguration;
import com.bux.bot.basic_trading_bot.exception.InvalidBodyRequestException;
import com.bux.bot.basic_trading_bot.exception.InvalidBrokerConfigurationException;
import com.bux.bot.basic_trading_bot.exception.WebClientApiCallException;
import com.bux.bot.basic_trading_bot.exception.WebClientInitializationException;
import com.bux.bot.basic_trading_bot.util.JsonUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@EnableConfigurationProperties(value = BrokersConfiguration.class)
@ContextConfiguration(classes = {BrokersConfiguration.class})
@ExtendWith(SpringExtension.class)
@SpringBootTest
class BuxTradeServiceUnitsTest {


  public static MockWebServer mockBackEnd;
  @Autowired
  BrokersConfiguration brokersConfiguration;

  BuxTradeService buxTradeService;

  @BeforeAll
  static void setUp() throws IOException {
    mockBackEnd = new MockWebServer();
    mockBackEnd.start();
  }

  @AfterAll
  static void tearDown() throws IOException {
    mockBackEnd.shutdown();
  }

  @BeforeEach
  void initialize() throws InvalidBrokerConfigurationException {
    String baseUrl = String.format("http://localhost:%s", mockBackEnd.getPort());
    this.brokersConfiguration.getBux().getRest().setBaseUrl(baseUrl);
    BuxWebClientFactory buxWebClientFactory = new BuxWebClientFactory(this.brokersConfiguration);
    buxTradeService = new BuxTradeService(buxWebClientFactory);
  }


  @Test
  void testOpenLongPositionTest()
          throws WebClientInitializationException, InvalidBrokerConfigurationException, InvalidBodyRequestException, JsonProcessingException {
    // given

    OpenPositionRequest openpositionRequest=JsonUtil.jsonToObject(MockData.OPEN_POSITION_REQUEST,OpenPositionRequest.class);
    OpenPositionResponse openPositionResponse=JsonUtil.jsonToObject(MockData.SUCCESSFUL_OPEN_POSITION_RESPONSE,OpenPositionResponse.class);
    String productId = openpositionRequest.getProductId();
    String currency = openpositionRequest.getInvestingAmount().getCurrency();
    int leverage = openpositionRequest.getLeverage();
    String amount = openpositionRequest.getInvestingAmount().getAmount();
    int decimals = openpositionRequest.getInvestingAmount().getDecimals();
    String positionId = openPositionResponse.getPositionId();
    // adding expected response to mockBackEnd
    mockBackEnd.enqueue(
            new MockResponse()
                    .setBody(JsonUtil.toJsonFormat(openPositionResponse))
                    .setStatus(HttpStatus.Series.SUCCESSFUL.toString())
                    .addHeader("Content-Type", "application/json"));
    // when
    Mono<OpenPositionResponse> responseMono =
            buxTradeService.openLongPosition(productId, amount, leverage, decimals, currency);
    // then
    StepVerifier.create(responseMono)
            .expectNextMatches(
                    response ->
                            response.getPositionId().equals(positionId)
                                    && response.getProduct().getSecurityId().equals(productId))
            .verifyComplete();
  }

  @Test
  void testSuccessfulClosePositionTest()
          throws WebClientInitializationException, InvalidBrokerConfigurationException, InvalidBodyRequestException, JsonProcessingException {
    // given
    String positionId = "4c58a0b2-ea78-46a0-ac21-5a8c22d527dc";
    String reponseString= MockData.SUCCESSFUL_CLOSE_POSITION_RESPONSE;
    ClosePositionResponse closePositionResponse=JsonUtil.jsonToObject(reponseString,ClosePositionResponse.class);
    // adding expected response to mockBackEnd
    mockBackEnd.enqueue(
            new MockResponse()
                    .setBody(JsonUtil.toJsonFormat(closePositionResponse))
                    .addHeader("Content-Type", "application/json"));
    // when
    Mono<ClosePositionResponse> responseMono =
            buxTradeService.closePosition(positionId);
    // then
    StepVerifier.create(responseMono)
            .expectNextMatches(
                    response ->
                            response.getPositionId().equals(positionId)
                                    && response.getDirection().equals("SELL"))

            .verifyComplete();
  }
  @Test
  void testFailedClosePositionTest()
          throws WebClientInitializationException, InvalidBrokerConfigurationException, JsonProcessingException {
    // given
    String positionId = "4c58a0b2-ea78-46a0-ac21-5a8c22d527dc";
    String reponseString= MockData.FAILED_CLOSE_POSITION_RESPONSE;
    ErrorResponse closePositionResponse=JsonUtil.jsonToObject(reponseString,ErrorResponse.class);
    WebClientApiCallException expectedException = new WebClientApiCallException(reponseString);
    // adding expected response to mockBackEnd
    mockBackEnd.enqueue(
            new MockResponse()
                    .setBody(JsonUtil.toJsonFormat(closePositionResponse))
                    .setStatus(HttpStatus.Series.CLIENT_ERROR.toString())
                    .addHeader("Content-Type", "application/json"));
    // when
    Mono<ClosePositionResponse> responseMono =
            buxTradeService.closePosition(positionId);
    // then
    StepVerifier.create(responseMono)
            .expectError(WebClientApiCallException.class);
  }

  @Test
  void testOpenLongPositionWebClientInitializationExceptionTest()
          throws WebClientInitializationException {
    assertThrows(
            WebClientInitializationException.class,
            () -> (new BuxTradeService(null)).openLongPosition("42", "10", 1, 1, "GBP"));
  }

  @Test
  void testClosePositionWebClientInitializationExceptionTest() throws InvalidBrokerConfigurationException, WebClientInitializationException {
    assertThrows(WebClientInitializationException.class, () -> (new BuxTradeService(null)).closePosition("42"));
    assertThrows(WebClientInitializationException.class,
            () -> (new BuxTradeService(null)).closePosition("broker.bux configuration not set"));
  }
  @Test
  void testValidateOpenPositionRequest() throws InvalidBodyRequestException {
    OpenPositionRequest openPositionRequest = new OpenPositionRequest();
    openPositionRequest.setRiskWarningConfirmation("Risk Warning Confirmation");
    openPositionRequest.setDirection("Direction");
    openPositionRequest.setSource(null);
    openPositionRequest.setProductId("42");
    openPositionRequest.setLeverage(1);
    openPositionRequest.setInvestingAmount(new InvestingAmount("GBP", 1, "10"));
    this.buxTradeService.validateOpenPositionRequest(openPositionRequest);
    assertEquals("Direction", openPositionRequest.getDirection());
    assertEquals("OpenPositionRequest(productId=42, investingAmount=InvestingAmount(currency=GBP, decimals=1,"
            + " amount=10), leverage=1, direction=Direction, source=null, riskWarningConfirmation=Risk Warning"
            + " Confirmation)", openPositionRequest.toString());
    assertEquals("Risk Warning Confirmation", openPositionRequest.getRiskWarningConfirmation());
    assertEquals("42", openPositionRequest.getProductId());
    assertEquals(1, openPositionRequest.getLeverage());
  }
  @Test
  void testValidateOpenPositionRequest2() throws InvalidBodyRequestException {
    OpenPositionRequest openPositionRequest = new OpenPositionRequest();
    openPositionRequest.setRiskWarningConfirmation("Risk Warning Confirmation");
    openPositionRequest.setDirection("Direction");
    openPositionRequest.setSource(null);
    openPositionRequest.setProductId("42");
    openPositionRequest.setLeverage(0);
    openPositionRequest.setInvestingAmount(new InvestingAmount("GBP", 1, "10"));
    assertThrows(InvalidBodyRequestException.class,
            () -> this.buxTradeService.validateOpenPositionRequest(openPositionRequest));
  }

  @Test
  void testValidateOpenPositionRequest3() throws InvalidBodyRequestException {
    OpenPositionRequest openPositionRequest = new OpenPositionRequest();
    openPositionRequest.setRiskWarningConfirmation("Risk Warning Confirmation");
    openPositionRequest.setDirection("Direction");
    openPositionRequest.setSource(null);
    openPositionRequest.setProductId("42");
    openPositionRequest.setLeverage(1);
    openPositionRequest.setInvestingAmount(null);
    assertThrows(InvalidBodyRequestException.class,
            () -> this.buxTradeService.validateOpenPositionRequest(openPositionRequest));
  }


}
