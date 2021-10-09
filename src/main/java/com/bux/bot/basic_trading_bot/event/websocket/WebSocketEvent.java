package com.bux.bot.basic_trading_bot.event.websocket;

import com.bux.bot.basic_trading_bot.dto.WebSocketEventMessage;

public class WebSocketEvent {
  private WebSocketStatusEventType event;
  private WebSocketEventMessage message;
  private boolean isEmitted = false;


  public WebSocketEvent(WebSocketStatusEventType event, WebSocketEventMessage message) {
    this.event = event;
    this.message = message;
  }

  public static WebSocketEvent createConnectedEvent(WebSocketEventMessage message) {
    return new WebSocketEvent(WebSocketStatusEventType.CONNECTED, message);
  }

  public static WebSocketEvent createDisconnectedEvent(WebSocketEventMessage message) {
    return new WebSocketEvent(WebSocketStatusEventType.DISCONNECTED, message);
  }

  public static WebSocketEvent createInputMessageEvent(WebSocketEventMessage message) {
    return new WebSocketEvent(WebSocketStatusEventType.IN_MESSAGE, message);
  }

  public static WebSocketEvent createOutputMessageEvent(WebSocketEventMessage message) {
    return new WebSocketEvent(WebSocketStatusEventType.OUT_MESSAGE, message);
  }

  public boolean isEmitted() {
    return isEmitted;
  }

  public void setEmitted(boolean emitted) {
    isEmitted = emitted;
  }

  public WebSocketEventMessage getMessage() {
    return message;
  }

  public WebSocketStatusEventType getEvent() {
    return event;
  }
}
