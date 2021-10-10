package com.bux.bot.basic_trading_bot.client.websocket.bux_impl;

import com.bux.bot.basic_trading_bot.dto.ProductPrice;
import com.bux.bot.basic_trading_bot.dto.UnSubscribeMessage;
import com.bux.bot.basic_trading_bot.dto.WebSocketEventMessage;
import com.bux.bot.basic_trading_bot.event.websocket.WebSocketEvent;
import com.bux.bot.basic_trading_bot.event.websocket.WebSocketEventBus;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.server.reactive.ChannelSendOperator;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ContextConfiguration(
    classes = {
      BuxTrackerService.class,
      BuxWebSocketClient.class,
      WebSocketEventBus.class,
      AsyncHelperService.class
    })
@ExtendWith(SpringExtension.class)
class BuxTrackerServiceTest {
  @Autowired private BuxTrackerService buxTrackerService;

  @MockBean private BuxWebSocketClient buxWebSocketClient;

  @Autowired private WebSocketEventBus webSocketEventBus;
  @Autowired private AsyncHelperService asyncHelperService;

  @Test
  void testConnect() {
    // given
    Publisher<Object> publisher = (Publisher<Object>) mock(Publisher.class);
    doNothing().when(publisher).subscribe((org.reactivestreams.Subscriber<? super Object>) any());
    ChannelSendOperator channelSendOperator =
        new ChannelSendOperator(
            publisher, (Function<Publisher<Object>, Publisher<Void>>) mock(Function.class));

    when(this.buxWebSocketClient.getConnection()).thenReturn(channelSendOperator);
    // when
    this.buxTrackerService.connect().subscribe();

    // then
    verify(this.buxWebSocketClient).getConnection();
    verify(publisher).subscribe((org.reactivestreams.Subscriber<? super Object>) any());
  }

  @Test
  void testConnectWhenErrorOnConnection() throws InterruptedException {
    // given
    when(this.buxWebSocketClient.getConnection())
        .
        // first error on connect
        thenReturn(Mono.error(new Exception()))
        // then connect
        .thenReturn(Mono.empty());

    // when
    AtomicBoolean connected = new AtomicBoolean(false);
    this.buxTrackerService
        .connect()
        .subscribe(
            e -> {
              connected.set(e);
            });

    this.webSocketEventBus.emitToConnection(
        WebSocketEvent.createConnectedEvent(new WebSocketEventMessage("")));
    // then
    // verify(this.buxWebSocketClient).getConnection();
    assertTrue(connected.get());
  }

  @Test
  void testMonitorOnProductPrice() throws InterruptedException {
    // given
    String productId1 = "db3232";
    String productId2 = "hf6969";
    // when
    AtomicBoolean emittedForProduct1 = new AtomicBoolean(false);
    AtomicBoolean emittedForProduct2 = new AtomicBoolean(false);
    this.webSocketEventBus.subscribeOnOutput(
        e -> {
          if (e.getMessage().getContent().contains(productId1))
            ;
          emittedForProduct1.set(true);
          if (e.getMessage().getContent().contains(productId2))
            ;
          emittedForProduct2.set(true);
        });
    this.buxTrackerService.monitorProductPrice(productId1);
    this.buxTrackerService.monitorProductPrice(productId1);
    this.buxTrackerService.monitorProductPrice(productId2);

    // then
    assertThat(this.buxTrackerService.getProductSubscribeCount().get(productId1)).isEqualTo(2);
    assertThat(this.buxTrackerService.getProductSubscribeCount().get(productId2)).isEqualTo(1);
    assertTrue(emittedForProduct1.get());
    assertTrue(emittedForProduct2.get());
  }

  @Test
  void testUnsubscribeOnProductPrice() throws InterruptedException {
    // given
    String productId1 = "db324432";
    String productId2 = "hf6944469";
    this.buxTrackerService.monitorProductPrice(productId1);
    this.buxTrackerService.monitorProductPrice(productId1);
    this.buxTrackerService.monitorProductPrice(productId2);
    AtomicBoolean emittedForUnsubscribeProduct2 = new AtomicBoolean(false);
    this.webSocketEventBus.subscribeOnOutput(
        e -> {
          if (e.getMessage()
              .getContent()
              .equals(
                  new UnSubscribeMessage(Constants.TRADING_PRODUCT_PREFIX + productId2).toString()))
            ;
          emittedForUnsubscribeProduct2.set(true);
        });

    // when
    this.buxTrackerService.unsubscribeOnProductPrice(productId2);
    this.buxTrackerService.unsubscribeOnProductPrice(productId1);

    // then
    assertThat(this.buxTrackerService.getProductSubscribeCount().get(productId1)).isEqualTo(1);
    assertThat(this.buxTrackerService.getProductSubscribeCount().get(productId2)).isEqualTo(0);
    assertThat(emittedForUnsubscribeProduct2.get()).isTrue();
  }

  @Test
  void testSubscribeOnAllPrice() throws JsonProcessingException {
    // given
    String productId1 = "gt87283";
    String price1 = "399";
    String price2 = "400";
    ProductPrice productPrice1 = new ProductPrice(productId1, price1);
    ProductPrice productPrice2 = new ProductPrice(productId1, price2);
    // when
    Flux<ProductPrice> stream = this.buxTrackerService.subscribeOnAllProductPrice();


    // then
    StepVerifier.create(stream)
        .expectNextMatches(
            pp -> pp.getSecurityId().equals(productId1) && pp.getCurrentPrice().equals(price1))
        .expectNextMatches(
            pp -> pp.getSecurityId().equals(productId1) && pp.getCurrentPrice().equals(price2));

    this.asyncHelperService.emitProductPrice(this.webSocketEventBus, productPrice1, productPrice2);
  }

  @Test
  void testSubscribeOnProductPrice() throws JsonProcessingException {
    // given
    String productId1 = "gt87283";
    String productId2 = "gt87232";
    String price1 = "399";
    String price2 = "400";
    ProductPrice productPrice1 = new ProductPrice(productId1, price1);
    ProductPrice productPrice2 = new ProductPrice(productId2, price2);

    // when
    Flux<ProductPrice> stream = this.buxTrackerService.subscribeOnProductPrice(productId1);

    // then

    StepVerifier.create(stream)
        .expectNextMatches(
            pp -> pp.getSecurityId().equals(productId1) && pp.getCurrentPrice().equals(price1));

    this.asyncHelperService.emitProductPrice(this.webSocketEventBus, productPrice1, productPrice2);
  }
}
