package com.bux.bot.basic_trading_bot.client.websocket.bux_impl;

import com.bux.bot.basic_trading_bot.config.BrokersConfiguration;
import com.bux.bot.basic_trading_bot.exception.InvalidBrokerConfigurationException;
import com.bux.bot.basic_trading_bot.exception.Messages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;
import reactor.core.publisher.Mono;

import java.net.URI;

@Service
public class BuxWebSocketClient {
  Logger logger = LoggerFactory.getLogger(BuxWebSocketClient.class);


  private final BuxWebSocketHandler buxWebSocketHandler;
  private final BrokersConfiguration brokersConfiguration;
  private String baseUrl;
  private String channelUrl;
  private String accessToken;

  public BuxWebSocketClient(
      BuxWebSocketHandler buxWebSocketHandler, BrokersConfiguration brokersConfiguration)
      throws InvalidBrokerConfigurationException {
    this.buxWebSocketHandler = buxWebSocketHandler;
    this.brokersConfiguration = brokersConfiguration;
    initFromConfiguration();
  }

  /***
   * making connection to websocket provider server
   * @return
   */
  public Mono<Void> getConnection() {
    reloadConfig();
    String url = String.format("%s%s", baseUrl, channelUrl);
    logger.info("connecting to " + url);
    HttpHeaders headers = new HttpHeaders();
    headers.add("Accept-Language", "posnl-NL,en;q=0.8");
    headers.add("Authorization", "Bearer " + accessToken);
    WebSocketClient client = new ReactorNettyWebSocketClient();
    return client.execute(URI.create(url), headers, buxWebSocketHandler);
  }

  private void reloadConfig() {
    try {
      initFromConfiguration();
    } catch (InvalidBrokerConfigurationException e) {
      logger.error("websocket config error",e);
    }
  }

  /***
   * initializing connection config from properties
   * @throws InvalidBrokerConfigurationException
   */
  private void initFromConfiguration() throws InvalidBrokerConfigurationException {
    // handling exception
    if (brokersConfiguration == null)
      throw new InvalidBrokerConfigurationException(Messages.BROKER_CONFIGURATION_NOT_SET);
    if (brokersConfiguration.getBux() == null)
      throw new InvalidBrokerConfigurationException(Messages.BROKER_BUX_CONFIGURATION_NOT_SET);
    if (brokersConfiguration.getBux().getWebsocket() == null)
      throw new InvalidBrokerConfigurationException(
          Messages.BROKER_BUX_WEBSOCKET_CONFIGURATION_NOT_SET);
    if (brokersConfiguration.getBux().getWebsocket().getBaseUrl() == null)
      throw new InvalidBrokerConfigurationException(
          Messages.BROKER_BUX_WEBSOCKET_BASE_URL_CONFIGURATION_NOT_SET);
    if (brokersConfiguration.getBux().getWebsocket().getChannelUrl() == null)
      throw new InvalidBrokerConfigurationException(
          Messages.BROKER_BUX_WEBSOCKET_CHANNEL_URL_CONFIGURATION_NOT_SET);
    if (brokersConfiguration.getBux().getWebsocket().getAccessToken() == null)
      throw new InvalidBrokerConfigurationException(
          Messages.BROKER_BUX_WEBSOCKET_ACCESS_TOKEN_CONFIGURATION_NOT_SET);
    // setting variables
    baseUrl = brokersConfiguration.getBux().getWebsocket().getBaseUrl();
    channelUrl = brokersConfiguration.getBux().getWebsocket().getChannelUrl();
    accessToken = brokersConfiguration.getBux().getWebsocket().getAccessToken();
  }
}
