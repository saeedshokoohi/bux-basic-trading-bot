package com.bux.bot.basic_trading_bot.client.websocket.bux_impl;

import com.bux.bot.basic_trading_bot.client.websocket.TrackerService;
import com.bux.bot.basic_trading_bot.dto.ProductPrice;
import com.bux.bot.basic_trading_bot.dto.SubscribeMessage;
import com.bux.bot.basic_trading_bot.dto.UnSubscribeMessage;
import com.bux.bot.basic_trading_bot.dto.WebSocketEventMessage;
import com.bux.bot.basic_trading_bot.event.websocket.WebSocketEvent;
import com.bux.bot.basic_trading_bot.event.websocket.WebSocketEventBus;
import com.bux.bot.basic_trading_bot.service.StartupService;
import com.bux.bot.basic_trading_bot.util.JsonUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static com.bux.bot.basic_trading_bot.event.websocket.WebSocketStatusEventType.CONNECTED;

@Service
public class BuxTrackerService implements TrackerService {
  private final BuxWebSocketClient buxWebSocketClient;
  private final WebSocketEventBus webSocketEventBus;
  Logger logger = LoggerFactory.getLogger(StartupService.class);
  //
  private ConcurrentMap<String, Integer> productSubscribeCount = new ConcurrentHashMap<>();
  private List<WebSocketEvent> subscribedEvents = Collections.synchronizedList(new ArrayList<>());

  public BuxTrackerService(
      WebSocketEventBus webSocketEventBus, BuxWebSocketClient buxWebSocketClient) {
    this.webSocketEventBus = webSocketEventBus;
    this.buxWebSocketClient = buxWebSocketClient;
    subscribeOnInitialEvents();
  }

  @Override
  public Mono<Boolean> connect() {
    return Mono.create(
        emitter -> {
          // subscribing on connection event to check connection
          webSocketEventBus.subscribeOnConnection(
              event -> {
                if (CONNECTED.equals(event.getEvent())) emitter.success(true);
              });
          // asking for connection
          buxWebSocketClient
              .getConnection()
              .doOnError(
                  e -> {
                    webSocketEventBus.emitToConnection(
                        WebSocketEvent.createDisconnectedEvent(new WebSocketEventMessage("")));
                  })
              .subscribe();
        });
  }

  @Override
  public void monitorProductPrice(String productId) {

    if (!productSubscribeCount.containsKey(productId)
        || productSubscribeCount.get(productId) == 0) {
      WebSocketEventMessage message =
          new WebSocketEventMessage(
              new SubscribeMessage(Constants.TRADING_PRODUCT_PREFIX + productId).toString());
      WebSocketEvent event = WebSocketEvent.createOutputMessageEvent(message);
      emit(event);
      logger.info("monitoring price for productId :" + productId);
    }
    if (productSubscribeCount.containsKey(productId)) {
      Integer count = productSubscribeCount.get(productId) + 1;
      productSubscribeCount.replace(productId, count);

    } else {
      productSubscribeCount.put(productId, 1);
    }
  }

  public Flux<ProductPrice> subscribeOnProductPrice(String productId) {

    return Flux.create(
        emitter -> {
          subscribeOnAllProductPrice()
              .subscribe(
                  productPrice -> {
                    if (productPrice.getSecurityId().equals(productId)) emitter.next(productPrice);
                  });
        });
  }

  public Flux<ProductPrice> subscribeOnAllProductPrice() {
    return Flux.create(
        emitter -> {
          webSocketEventBus.subscribeOnInput(
              e -> {
                ProductPrice productPrice = mapToProductPrice(e);
                if (productPrice != null) emitter.next(productPrice);
              });
        });
  }

  @Override
  public void unsubscribeOnProductPrice(String productId) {
    int count = 0;
    if (productSubscribeCount.containsKey(productId)) {
      if (productSubscribeCount.get(productId) > 0) {

        count = productSubscribeCount.get(productId) - 1;
        productSubscribeCount.replace(productId, count);
      }
    }
    if (count == 0) {
      logger.info("unsubscribing on product :" + productId);
      WebSocketEventMessage message =
          new WebSocketEventMessage(
              new UnSubscribeMessage(Constants.TRADING_PRODUCT_PREFIX + productId).toString());
      WebSocketEvent event = WebSocketEvent.createOutputMessageEvent(message);
      emit(event);
    }
  }

  private ProductPrice mapToProductPrice(WebSocketEvent e) {
    if (e == null || e.getMessage() == null || e.getMessage().getContent() == null) return null;
    ProductPrice productPrice = null;
    String eventMessage = e.getMessage().getContent();
    String type = JsonUtil.getFieldValue(eventMessage, Constants.TYPE);
    JsonNode body = JsonUtil.getFieldValueAsJsonNode(eventMessage, Constants.BODY);
    if (type != null && Constants.TRADING_QUOTE.equals(type) && body != null) {
      try {
        productPrice = JsonUtil.jsonToObject(body.toString(), ProductPrice.class);
      } catch (JsonProcessingException ex) {

      }
    }
    return productPrice;
  }

  public ConcurrentMap<String, Integer> getProductSubscribeCount() {
    return productSubscribeCount;
  }

  private void reconnect() {
    logger.info("reconnecting to web socket server.....");
    // todo: checking some policies for reconnecting
    // todo : temporary we wait for some miliseconds and will try again
    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    buxWebSocketClient.getConnection().subscribe();
  }

  private void emit(WebSocketEvent event) {
    this.webSocketEventBus.emitToOutput(event);
    addToSubscribedEvent(event);
  }

  private void addToSubscribedEvent(WebSocketEvent event) {
    this.subscribedEvents.add(event);
  }

  private void reSubscribeEvents() {
    this.subscribedEvents.forEach(
        e -> {
          if (!e.isEmitted()) this.webSocketEventBus.emitToOutput(e);
        });
  }

  private void subscribeOnInitialEvents() {
    this.webSocketEventBus.subscribeOnConnection(
        e -> {
          if (e != null && e.getEvent() != null)
            switch (e.getEvent()) {
              case DISCONNECTED:
                doOnDisconnectEvent();
                break;
              case CONNECTED:
                doOnReconnect();
                break;
            }
        });
  }

  private void doOnDisconnectEvent() {
    this.reconnect();
    this.subscribedEvents.forEach(e -> e.setEmitted(false));
  }

  private void doOnReconnect() {
    reSubscribeEvents();
  }
}
