package com.bux.bot.basic_trading_bot;

import com.bux.bot.basic_trading_bot.config.BrokersConfiguration;
import com.bux.bot.basic_trading_bot.entity.BotOrderInfo;
import com.bux.bot.basic_trading_bot.exception.InvalidBrokerConfigurationException;
import com.bux.bot.basic_trading_bot.repository.BotOrderInfoRepository;
import com.bux.bot.basic_trading_bot.service.BotOrderInfoService;
import com.bux.bot.basic_trading_bot.service.StartupService;
import lombok.SneakyThrows;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static com.bux.bot.basic_trading_bot.client.rest.bux_impl.TraderServerMockData.SUCCESSFUL_CLOSE_POSITION_RESPONSE_productp32112;
import static com.bux.bot.basic_trading_bot.client.rest.bux_impl.TraderServerMockData.SUCCESSFUL_OPEN_POSITION_RESPONSE_productp32112;
import static com.bux.bot.basic_trading_bot.client.websocket.bux_impl.WebSocketMockData.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class BasicTradingBotApplicationIntegrationTests {

  MockWebServer mockBackRest;
  MockWebServer mockBackWebSocket;
  @Autowired BotOrderInfoRepository botOrderInfoRepository;
  @Autowired BrokersConfiguration brokersConfiguration;
  @Autowired StartupService startupService;
  @Autowired BotOrderInfoService botOrderInfoService;

  @BeforeEach
  void initialize() throws InvalidBrokerConfigurationException, IOException {
    mockBackRest = new MockWebServer();
    mockBackWebSocket = new MockWebServer();
    mockBackRest.start();
    mockBackWebSocket.start();
    String baseUrlForRest = String.format("http://localhost:%s", mockBackRest.getPort());
    String baseUrlForWebSocket = String.format("http://localhost:%s", mockBackWebSocket.getPort());
    this.brokersConfiguration.getBux().getRest().setBaseUrl(baseUrlForRest);
    this.brokersConfiguration.getBux().getWebsocket().setBaseUrl(baseUrlForWebSocket);
  }

  @AfterEach
  void dispose() throws IOException {

    mockBackRest.shutdown();
    mockBackWebSocket.shutdown();
  }

  @Test
  void testStartUpService() throws InterruptedException, IOException {
    // given
    initWebSocketMockServer();
    initTraderMockServer();
    List<BotOrderInfo> addedRecords = addSomeSampleRecords();
    // when
    this.startupService.startTradingBot();
    Thread.sleep(20000);
    Optional<BotOrderInfo> targetBotOrder = this.botOrderInfoRepository.findById(1l);
    // then
    assertThat(targetBotOrder.isPresent()).isTrue().as("bot order created");
    assertThat(targetBotOrder.get())
        .matches(bo -> bo.getOpenPosition() != null, "position opened and saved in bot orderInfo");
    assertThat(targetBotOrder.get())
        .matches(bo -> bo.getClosePosition() != null, "position closed and saved in bot orderInfo");
  }

  private void initTraderMockServer() {
    mockBackRest.enqueue(
        new MockResponse()
            .setBody(SUCCESSFUL_OPEN_POSITION_RESPONSE_productp32112)
            .addHeader("Content-Type", "application/json"));

    mockBackRest.enqueue(
        new MockResponse()
            .setBody(SUCCESSFUL_CLOSE_POSITION_RESPONSE_productp32112)
            .addHeader("Content-Type", "application/json"));
  }

  private List<BotOrderInfo> addSomeSampleRecords() {

    BotOrderInfo botOrder1 = new BotOrderInfo("order1", "p32112", "100", 100.0, 110.0, 95.0);
    BotOrderInfo botOrder2 = new BotOrderInfo("order2", "p8562", "100", 100.0, 110.0, 95.0);
    BotOrderInfo botOrder3 = new BotOrderInfo("order3", "p4857", "100", 100.0, 110.0, 95.0);
    List<BotOrderInfo> retList = List.of(botOrder1, botOrder2, botOrder3);
    botOrderInfoService.addNewBotOrderInfo(botOrder1).block();
    botOrderInfoService.addNewBotOrderInfo(botOrder2).block();
    botOrderInfoService.addNewBotOrderInfo(botOrder3).block();
    return retList;
  }

  private void initWebSocketMockServer() {
    this.mockBackWebSocket.enqueue(
        new MockResponse()
            .withWebSocketUpgrade(
                new WebSocketListener() {
                  @Override
                  public void onOpen(WebSocket webSocket, Response response) {
                    webSocket.send(connectionResponse);
                  }

                  @SneakyThrows
                  @Override
                  public void onMessage(WebSocket webSocket, String string) {
                    // simulation response from websocket
                    // sending prices
                    int i = 5;
                    while (i > 0) {
                      i--;
                      try {
                        Thread.sleep(2000);
                      } catch (InterruptedException e) {
                        e.printStackTrace();
                      }
                      webSocket.send(INPUT_MESSAGE_productp32112);
                    }
                    // sending good price to buy
                    webSocket.send(INPUT_MESSAGE_productp32112_goodPrice_tobuy);
                    Thread.sleep(3000);
                    // sending good price to sell
                    webSocket.send(INPUT_MESSAGE_productp32112_goodPrice_tosellWithProfit);
                    webSocket.send(INPUT_MESSAGE_productp32112_goodPrice_tosellWithProfit);
                    Thread.sleep(3000);
                    webSocket.close(1, "");
                  }

                  @Override
                  public void onClosing(WebSocket webSocket, int code, String reason) {
                    System.out.println("server onClosing");
                    System.out.println("code:" + code + " reason:" + reason);
                  }

                  @Override
                  public void onClosed(WebSocket webSocket, int code, String reason) {
                    System.out.println("server onClosed");
                    System.out.println("code:" + code + " reason:" + reason);
                  }

                  @Override
                  public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                    System.out.println("server onFailure");
                    System.out.println("throwable:" + t);
                    System.out.println("response:" + response);
                  }
                }));
  }
}
