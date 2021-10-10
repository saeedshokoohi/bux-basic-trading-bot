package com.bux.bot.basic_trading_bot.client.websocket.bux_impl;

import com.bux.bot.basic_trading_bot.dto.ProductPrice;
import com.bux.bot.basic_trading_bot.dto.WebSocketEventMessage;
import com.bux.bot.basic_trading_bot.event.websocket.WebSocketEvent;
import com.bux.bot.basic_trading_bot.event.websocket.WebSocketEventBus;
import com.bux.bot.basic_trading_bot.util.JsonUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class AsyncHelperService {

    @Async
    public CompletableFuture emitProductPrice(WebSocketEventBus webSocketEventBus, ProductPrice productPrice1, ProductPrice productPrice2) throws JsonProcessingException {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        webSocketEventBus.emitToInput(
                WebSocketEvent.createInputMessageEvent(
                        new WebSocketEventMessage(JsonUtil.toJsonFormat(productPrice1))));
        webSocketEventBus.emitToInput(
                WebSocketEvent.createInputMessageEvent(
                        new WebSocketEventMessage(JsonUtil.toJsonFormat(productPrice2))));
        return CompletableFuture.completedFuture(null);
    }
}
