package com.bux.bot.basic_trading_bot.event;

import com.bux.bot.basic_trading_bot.event.global.GlobalEvent;
import com.bux.bot.basic_trading_bot.event.global.GlobalEventBus;
import com.bux.bot.basic_trading_bot.event.global.GlobalEventType;
import com.bux.bot.basic_trading_bot.event.websocket.WebSocketEvent;
import com.bux.bot.basic_trading_bot.event.websocket.WebSocketEventBus;
import org.springframework.stereotype.Service;

@Service
public class EventAggregatorService {
  final GlobalEventBus globalEventBus;
  final WebSocketEventBus webSocketEventBus;

  public EventAggregatorService(
      GlobalEventBus buxTrackerService, WebSocketEventBus webSocketEventBus) {
    this.globalEventBus = buxTrackerService;
    this.webSocketEventBus = webSocketEventBus;

    passEventsFromWebSocketEventBusToGlobalEventBus();
  }

  /***
   * this method subscribe on WebSocket Event bus and pass the emitted events to GlobalEvent bus
   */
  private void passEventsFromWebSocketEventBusToGlobalEventBus() {
    this.webSocketEventBus.subscribeOnConnection(
        e -> globalEventBus.emit(
            new GlobalEvent<WebSocketEvent>(GlobalEventType.WEBSOCKET_CONNECTION, this, e)));
    this.webSocketEventBus.subscribeOnInput(
        e -> globalEventBus.emit(
            new GlobalEvent<WebSocketEvent>(GlobalEventType.WEBSOCKET_INPUT, this, e)));
    this.webSocketEventBus.subscribeOnOutput(
        e -> globalEventBus.emit(
            new GlobalEvent<WebSocketEvent>(GlobalEventType.WEBSOCKET_OUTPUT, this, e)));
  }
}
