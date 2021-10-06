package com.bux.bot.basic_trading_bot.event.websocket;

public interface WebSocketEventObserver {
    void next(WebSocketEvent event );

}
