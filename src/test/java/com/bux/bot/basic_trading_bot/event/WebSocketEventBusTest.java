package com.bux.bot.basic_trading_bot.event;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import com.bux.bot.basic_trading_bot.event.websocket.WebSocketEventBus;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ContextConfiguration(classes = {WebSocketEventBus.class})
@ExtendWith(SpringExtension.class)
class WebSocketEventBusTest {
    @Autowired
    private WebSocketEventBus webSocketEventBus;

  /*  @Test
    void testConstructor() {
        assertTrue((new WebSocketEventBus()).getObservers().isEmpty());
    }

    @Test
    void testSubscribe() {
        //given
        WebSocketEventObserver observer = mock(WebSocketEventObserver.class);
        //when
        this.webSocketEventBus.subscribe(observer);
        //then
        assertTrue(this.webSocketEventBus.getObservers().contains(observer));
    }

    @Test
    void testEmit() {
        //given
        WebSocketEventMessage message=new WebSocketEventMessage("Message to emit");
        AtomicReference<WebSocketEventMessage> subscribedMessage= new AtomicReference<>(null);
        AtomicReference<WebSocketStatusEventType> subscribedEvent=new AtomicReference<>(null);
        WebSocketEvent createConnectedEventResult = WebSocketEvent.createInputMessageEvent(message);
        WebSocketEventObserver observer1 = (e)->{
            subscribedMessage.set(e.getMessage());
            subscribedEvent.set(e.getEvent());
        };
        this.webSocketEventBus.subscribe(observer1);
        //when
        this.webSocketEventBus.emit(createConnectedEventResult);
        //then
        assertEquals(subscribedMessage.get().getContent(),message.getContent());
        assertEquals(subscribedEvent.get(),createConnectedEventResult.getEvent());
    }

    @Test
    void testUnsubscribe() {
        //given
        WebSocketEventObserver observer1 = mock(WebSocketEventObserver.class);
        WebSocketEventObserver observer2 = mock(WebSocketEventObserver.class);
        this.webSocketEventBus.subscribe(observer1);
        //then
        assertFalse(this.webSocketEventBus.unsubscribe(observer2),"checking false for checking unscribtion for unAvailable observer");
        assertTrue(this.webSocketEventBus.unsubscribe(observer1),"checking true for unsubscribing available observer");
        assertFalse(this.webSocketEventBus.getObservers().contains(observer1),"checking if unsubscribed observer has removed from list of observer ");
    }*/
}

