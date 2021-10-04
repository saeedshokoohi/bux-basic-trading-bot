package com.bux.bot.basic_trading_bot.client.rest.bux_impl;

import com.bux.bot.basic_trading_bot.client.rest.bux_impl.dto.OpenPositionResponse;
import com.bux.bot.basic_trading_bot.client.rest.bux_impl.dto.Product;
import com.bux.bot.basic_trading_bot.config.BrokersConfiguration;
import com.bux.bot.basic_trading_bot.exception.InvalidBrokerConfigurationException;
import com.bux.bot.basic_trading_bot.exception.WebClientInitializationException;
import com.bux.bot.basic_trading_bot.util.JsonUtil;
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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertThrows;

@EnableConfigurationProperties(value = BrokersConfiguration.class)
@ContextConfiguration(classes = {BrokersConfiguration.class})
@ExtendWith(SpringExtension.class)
@SpringBootTest
class BuxTradeServiceTest {

  public static MockWebServer mockBackEnd;
  @Autowired BrokersConfiguration brokersConfiguration;

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
  void testConstructor() {}

  @Test
  void testOpenLongPositionTest()
      throws WebClientInitializationException, InvalidBrokerConfigurationException {
    // given
    String productId = "sb26493";
    String currency = "BUX";
    int leverage = 2;
    String amount = "3";
    int decimals = 2;
    String positionId = "12313434641234asdfsda";
    OpenPositionResponse openPositionResponse = new OpenPositionResponse();
    openPositionResponse.setPositionId(positionId);
    openPositionResponse.setProduct(new Product(productId, "", ""));
    // adding expected response to mockBackEnd
    mockBackEnd.enqueue(
        new MockResponse()
            .setBody(JsonUtil.toJsonFormat(openPositionResponse))
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
  void testOpenLongPositionWebClientInitializationExceptionTest()
      throws WebClientInitializationException {
    assertThrows(
        WebClientInitializationException.class,
        () -> (new BuxTradeService(null)).openLongPosition("42", "10", 1, 1, "GBP"));
  }
}
