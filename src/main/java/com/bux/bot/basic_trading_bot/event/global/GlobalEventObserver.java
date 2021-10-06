package com.bux.bot.basic_trading_bot.event.global;

import com.bux.bot.basic_trading_bot.event.websocket.WebSocketEvent;

public interface GlobalEventObserver<T> {
    void next(GlobalEvent<T> event );
}
