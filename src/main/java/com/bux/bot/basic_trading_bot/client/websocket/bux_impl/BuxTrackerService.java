package com.bux.bot.basic_trading_bot.client.websocket.bux_impl;

import com.bux.bot.basic_trading_bot.client.websocket.TrackerClientService;
import com.bux.bot.basic_trading_bot.client.websocket.bux_impl.dto.SubscribeMessage;
import com.bux.bot.basic_trading_bot.dto.WebSocketEventMessage;
import com.bux.bot.basic_trading_bot.event.WebSocketEvent;
import com.bux.bot.basic_trading_bot.event.WebSocketEventBus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.bux.bot.basic_trading_bot.event.types.WebSocketStatusEventType.CONNECTED;
import static com.bux.bot.basic_trading_bot.event.types.WebSocketStatusEventType.DISCONNECTED;

@Service
public class BuxTrackerService implements TrackerClientService {

  List<WebSocketEvent> subscribedEvents = Collections.synchronizedList(new ArrayList<>());
  final BuxWebSocketClient buxWebSocketClient;
  final WebSocketEventBus webSocketEventBus;

  public BuxTrackerService(
      WebSocketEventBus webSocketEventBus, BuxWebSocketClient buxWebSocketClient) {
    this.webSocketEventBus = webSocketEventBus;
    this.buxWebSocketClient = buxWebSocketClient;
    subscribeOnInitialEvents();
  }

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

  public Flux<WebSocketEventMessage> subscribeOnProductPrice(String productId) {
    WebSocketEventMessage message =
        new WebSocketEventMessage(new SubscribeMessage("trading.product." + productId).toString());
    WebSocketEvent event = WebSocketEvent.createOutputMessageEvent(message);
    emit(event);
    return Flux.create(
        emitter -> {
          webSocketEventBus.subscribeOnInput(
              e -> {
                emitter.next(e.getMessage());
              });
        });
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
            if(!e.isEmitted())
          this.webSocketEventBus.emitToOutput(e);
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
