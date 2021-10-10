package com.bux.bot.basic_trading_bot.client.websocket.bux_impl;

import com.bux.bot.basic_trading_bot.dto.WebSocketEventMessage;
import com.bux.bot.basic_trading_bot.event.websocket.WebSocketEvent;
import com.bux.bot.basic_trading_bot.event.websocket.WebSocketEventBus;
import com.bux.bot.basic_trading_bot.event.websocket.WebSocketStatusEventType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(
    classes = {
      BuxWebSocketHandler.class,
      WebSocketEventBus.class,
    })
class BuxWebSocketHandlerTest {

  @Autowired WebSocketEventBus webSocketEventBus;
  @Autowired BuxWebSocketHandler buxWebSocketHandler;

  @Test
  void testHandleInputMessage() throws InterruptedException {

    // given
    WebSocketSession session = mock(WebSocketSession.class);
    WebSocketMessage mockWebSocketMessage = mock(WebSocketMessage.class);
    when(session.textMessage((String) any())).thenReturn(mockWebSocketMessage);
    List<WebSocketMessage> inputMessages = new ArrayList<>();
    WebSocketMessage inputMessage1 = mock(WebSocketMessage.class);
    WebSocketMessage inputMessage2 = mock(WebSocketMessage.class);
    inputMessages.add(inputMessage1);
    inputMessages.add(inputMessage2);
    when(inputMessage1.getPayloadAsText()).thenReturn(WebSocketMockData.INPUT_MESSAGE1);
    when(inputMessage2.getPayloadAsText()).thenReturn(WebSocketMockData.INPUT_MESSAGE2);
    when(session.receive()).thenReturn(Flux.fromIterable(inputMessages));

    when(session.send(any()))
        .thenReturn(
            Mono.create(
                emitter -> {
                  emitter.success();
                }));

    //   when(webSocketEventBus.subscribeOnOutput(any())).thenReturn(outputMessages);
    // when
    List<WebSocketEvent> inputEvents = new ArrayList<>();
    this.webSocketEventBus.subscribeOnInput(
        in -> {
          inputEvents.add(in);
        });
    buxWebSocketHandler.handle(session).then().block();
    // then
    assertThat(inputEvents.size()).isEqualTo(inputMessages.size());
  }

  @Test
  void testHandleOutputMessage() {

    // given
    List<WebSocketEvent> outputMessagesList = new ArrayList<>();
    outputMessagesList.add(
        new WebSocketEvent(
            WebSocketStatusEventType.OUT_MESSAGE,
            new WebSocketEventMessage(WebSocketMockData.SUBSCRIBE_TO_MESSAGE_1)));
    outputMessagesList.add(
        new WebSocketEvent(
            WebSocketStatusEventType.OUT_MESSAGE,
            new WebSocketEventMessage(WebSocketMockData.SUBSCRIBE_TO_MESSAGE_2)));
    Flux<WebSocketEvent> outputMessages = Flux.fromIterable(outputMessagesList);
    WebSocketSession session = mock(WebSocketSession.class);
    WebSocketMessage mockWebSocketMessage = mock(WebSocketMessage.class);
    when(session.textMessage((String) any())).thenReturn(mockWebSocketMessage);
    when(session.receive()).thenReturn(Flux.empty());
    when(session.send(any())).thenReturn(Mono.empty());
    //   when(webSocketEventBus.subscribeOnOutput(any())).thenReturn(outputMessages);
    // when
    buxWebSocketHandler.handle(session).then();
    outputMessages.subscribe(
        e -> {
          webSocketEventBus.emitToOutput(e);
        });

    // then

    verify(session, times(1)).receive();
  }
}
