package com.bux.bot.basic_trading_bot.client.websocket.bux_impl;

import com.bux.bot.basic_trading_bot.dto.WebSocketEventMessage;
import com.bux.bot.basic_trading_bot.event.websocket.WebSocketEvent;
import com.bux.bot.basic_trading_bot.event.websocket.WebSocketEventBus;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.server.reactive.ChannelSendOperator;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ContextConfiguration(classes = {BuxTrackerService.class, BuxWebSocketClient.class, WebSocketEventBus.class})
@ExtendWith(SpringExtension.class)
class BuxTrackerServiceTest {
    @Autowired
    private BuxTrackerService buxTrackerService;

    @MockBean
    private BuxWebSocketClient buxWebSocketClient;

    @Autowired
    private WebSocketEventBus webSocketEventBus;

    @Test
    void testConnect() {
        //given
        Publisher<Object> publisher = (Publisher<Object>) mock(Publisher.class);
        doNothing().when(publisher).subscribe((org.reactivestreams.Subscriber<? super Object>) any());
        ChannelSendOperator channelSendOperator = new ChannelSendOperator(publisher,
                (Function<Publisher<Object>, Publisher<Void>>) mock(Function.class));

        when(this.buxWebSocketClient.getConnection()).thenReturn(channelSendOperator);
        //when
        this.buxTrackerService.connect();

        //then
        verify(this.buxWebSocketClient).getConnection();
        verify(publisher).subscribe((org.reactivestreams.Subscriber<? super Object>) any());
    }
    @Test
    void testConnectWhenErrorOnConnection() throws InterruptedException {
        //given
        when(this.buxWebSocketClient.getConnection()).
                thenReturn(Mono.error(new Exception()));

        //when
        AtomicBoolean connected= new AtomicBoolean(false);
       this.buxTrackerService.connect().subscribe(e->{
           connected.set(e);
       });

        Thread.sleep(5000);

        this.webSocketEventBus.emitToConnection(WebSocketEvent.createConnectedEvent(new WebSocketEventMessage("")));


        //then
        verify(this.buxWebSocketClient).getConnection();
        assertTrue(connected.get());


    }

    @Test
    void testMonitorProductPrice() {
        // TODO: This test is incomplete.
        //   Reason: No meaningful assertions found.
        //   Diffblue Cover was unable to create an assertion.
        //   Make sure that fields modified by monitorProductPrice(String)
        //   have package-private, protected, or public getters.
        //   See https://diff.blue/R004 to resolve this issue.

        doNothing().when(this.webSocketEventBus)
                .emitToOutput((com.bux.bot.basic_trading_bot.event.websocket.WebSocketEvent) any());
        this.buxTrackerService.monitorProductPrice("42");
    }

    @Test
    void testMonitorProductPrice2() {
        // TODO: This test is incomplete.
        //   Reason: No meaningful assertions found.
        //   Diffblue Cover was unable to create an assertion.
        //   Make sure that fields modified by monitorProductPrice(String)
        //   have package-private, protected, or public getters.
        //   See https://diff.blue/R004 to resolve this issue.

        this.buxTrackerService.monitorProductPrice("Product Id");
    }

    @Test
    void testSubscribeOnProductPrice() {
        // TODO: This test is incomplete.
        //   Reason: No meaningful assertions found.
        //   Diffblue Cover was unable to create an assertion.
        //   Make sure that fields modified by subscribeOnProductPrice(String)
        //   have package-private, protected, or public getters.
        //   See https://diff.blue/R004 to resolve this issue.

        this.buxTrackerService.subscribeOnProductPrice("42");
    }

    @Test
    void testSubscribeOnAllProductPrice() {
        // TODO: This test is incomplete.
        //   Reason: No meaningful assertions found.
        //   Diffblue Cover was unable to create an assertion.
        //   Make sure that fields modified by subscribeOnAllProductPrice()
        //   have package-private, protected, or public getters.
        //   See https://diff.blue/R004 to resolve this issue.

        this.buxTrackerService.subscribeOnAllProductPrice();
    }

    @Test
    void testUnsubscribeOnProductPrice() {
        // TODO: This test is incomplete.
        //   Reason: No meaningful assertions found.
        //   Diffblue Cover was unable to create an assertion.
        //   Make sure that fields modified by unsubscribeOnProductPrice(String)
        //   have package-private, protected, or public getters.
        //   See https://diff.blue/R004 to resolve this issue.

        this.buxTrackerService.unsubscribeOnProductPrice("42");
    }
}

