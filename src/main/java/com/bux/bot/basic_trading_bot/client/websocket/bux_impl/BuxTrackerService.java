package com.bux.bot.basic_trading_bot.client.websocket.bux_impl;

import com.bux.bot.basic_trading_bot.client.websocket.TrackerService;
import com.bux.bot.basic_trading_bot.dto.ProductPrice;
import com.bux.bot.basic_trading_bot.dto.SubscribeMessage;
import com.bux.bot.basic_trading_bot.dto.UnSubscribeMessage;
import com.bux.bot.basic_trading_bot.dto.WebSocketEventMessage;
import com.bux.bot.basic_trading_bot.event.websocket.WebSocketEvent;
import com.bux.bot.basic_trading_bot.event.websocket.WebSocketEventBus;
import com.bux.bot.basic_trading_bot.util.JsonUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static com.bux.bot.basic_trading_bot.event.websocket.WebSocketStatusEventType.CONNECTED;
import static com.bux.bot.basic_trading_bot.event.websocket.WebSocketStatusEventType.DISCONNECTED;

@Service
public class BuxTrackerService implements TrackerService {
  ConcurrentMap<String,Integer> productSubscribeCount=new ConcurrentHashMap<>();
  List<WebSocketEvent> subscribedEvents = Collections.synchronizedList(new ArrayList<>());
  final BuxWebSocketClient buxWebSocketClient;
  final WebSocketEventBus webSocketEventBus;

  public BuxTrackerService(
      WebSocketEventBus webSocketEventBus, BuxWebSocketClient buxWebSocketClient) {
    this.webSocketEventBus = webSocketEventBus;
    this.buxWebSocketClient = buxWebSocketClient;
    subscribeOnInitialEvents();
  }

  @Override
  public Flux<WebSocketEventMessage> connect() {
    buxWebSocketClient
        .getConnection()
        .doOnError(
            e -> {
              System.out.println(e);

              reconnect();
            })
        .subscribe();
    return Flux.create(
        emitter -> {
          webSocketEventBus.subscribeOnConnection(
              event -> {
                if (CONNECTED.equals(event.getEvent())) emitter.next(event.getMessage());
                if (DISCONNECTED.equals(event.getEvent())) reconnect();
              });
        });
  }

  @Override
  public void monitorProductPrice(String productId) {
    if(!productSubscribeCount.containsKey(productId) || productSubscribeCount.get(productId)==0)
    {
      WebSocketEventMessage message =
              new WebSocketEventMessage(
                      new SubscribeMessage(Constants.TRADING_PRODUCT_PREFIX + productId).toString());
      WebSocketEvent event = WebSocketEvent.createOutputMessageEvent(message);
      emit(event);
    }
    if (productSubscribeCount.containsKey(productId)) {
      Integer count = productSubscribeCount.get(productId) + 1;
      productSubscribeCount.replace(productId,count);

    }else {
      productSubscribeCount.put(productId, 1);
    }
  }

  public Flux<ProductPrice> subscribeOnProductPrice(String productId) {
    return subscribeOnAllProductPrice()
        .filter(productPrice -> productPrice.getSecurityId().equals(productId));
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
    int count =0;
    if(productSubscribeCount.containsKey(productId))
    {
      if(productSubscribeCount.get(productId)>0)
      {
        count=count-1;
        productSubscribeCount.replace(productId,count);
      }
    }
    if (count == 0) {
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
    JsonNode body = JsonUtil.getFieldValueAsJsonNode(eventMessage,Constants.BODY);
    if (type!=null && Constants.TRADING_QUOTE.equals(type) && body != null) {
      try {
        productPrice = JsonUtil.jsonToObject(body.toString(), ProductPrice.class);
      } catch (JsonProcessingException ex) {

      }
    }
    return productPrice;
  }

  private void reconnect() {
    this.connect();
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
    this.subscribedEvents.forEach(e -> e.setEmitted(false));
  }

  private void doOnReconnect() {
    System.out.println("Reconnected to websocket");
    reSubscribeEvents();
  }
}
