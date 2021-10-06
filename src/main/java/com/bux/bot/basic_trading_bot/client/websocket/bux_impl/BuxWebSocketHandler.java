package com.bux.bot.basic_trading_bot.client.websocket.bux_impl;

import com.bux.bot.basic_trading_bot.dto.WebsocketInputMessage;
import com.bux.bot.basic_trading_bot.dto.enums.ConnectionStatus;
import com.bux.bot.basic_trading_bot.dto.WebSocketEventMessage;
import com.bux.bot.basic_trading_bot.event.websocket.WebSocketEvent;
import com.bux.bot.basic_trading_bot.event.websocket.WebSocketEventBus;
import com.bux.bot.basic_trading_bot.event.websocket.WebSocketStatusEventType;
import com.bux.bot.basic_trading_bot.util.JsonUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicReference;

import static com.bux.bot.basic_trading_bot.dto.enums.ConnectionStatus.CLOSED;
import static com.bux.bot.basic_trading_bot.dto.enums.ConnectionStatus.OPEN;

@Component
public class BuxWebSocketHandler implements WebSocketHandler {

  private WebSocketEventBus webSocketEventBus;
  private AtomicReference<ConnectionStatus>  status=new AtomicReference<>(CLOSED);

  public BuxWebSocketHandler(WebSocketEventBus webSocketEventBus) {
    this.webSocketEventBus = webSocketEventBus;
    subscribeOnInitialEvents();

  }

  @Override
  public Mono<Void> handle(WebSocketSession session) {
    // subscribing on outOut message Stream to send to websocket session
    Flux<WebSocketMessage> outMessages =
        this.outputMessages()
            .map(event -> event.getMessage().getContent())
            .map(session::textMessage);
    // listening on session
    // if any message recieved send it to the event handler
    Flux<WebSocketMessage> receiving =
        session
            .receive()
            .map(
                m -> {
                  WebSocketEvent event = createWebSocketEventFrom(m);
                  if (WebSocketStatusEventType.CONNECTED.equals(event.getEvent()) || WebSocketStatusEventType.DISCONNECTED.equals(event.getEvent())) {
                    status.set(WebSocketStatusEventType.CONNECTED.equals(event.getEvent()) ?OPEN:CLOSED);
                    webSocketEventBus.emitToConnection(event);
                  } else {
                    webSocketEventBus.emitToInput(event);
                  }
                  return m;
                })
            .doOnTerminate(
                () ->{
                  status.set(CLOSED);
                    webSocketEventBus.emitToConnection(
                        WebSocketEvent.createDisconnectedEvent(new WebSocketEventMessage("")));});
    return session.send(outMessages).mergeWith(receiving.then()).then();
  }

  private WebSocketEvent createWebSocketEventFrom(WebSocketMessage webSocketMessage) {

    String payload = webSocketMessage.getPayloadAsText();
    WebSocketEventMessage websocketEventMessage = new WebSocketEventMessage(payload);
    try {
      WebsocketInputMessage buxMessage =
          JsonUtil.jsonToObject(payload, WebsocketInputMessage.class);
      if (buxMessage.getT().equals("connect.connected")) {

        return WebSocketEvent.createConnectedEvent(websocketEventMessage);
      }
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }

    return WebSocketEvent.createInputMessageEvent(websocketEventMessage);
  }
  /***
   *
   * @return return stream of output messages from eventSource
   */
  private Flux<WebSocketEvent> outputMessages() {
    return Flux.create(
        emitter ->
            this.webSocketEventBus.subscribeOnOutput(
                event -> {
                  // listening on event handler
                  // if it is output Message send to outPutEventStream
                  // for sending to webSocketMessage
                  if (WebSocketStatusEventType.OUT_MESSAGE.equals(event.getEvent()))
                    if (OPEN.equals(status.get())) {
                      event.setEmitted(true);
                      emitter.next(event);
                    }
                }));
  }
  private void subscribeOnInitialEvents() {

  }

}
