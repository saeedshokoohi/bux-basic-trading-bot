package com.bux.bot.basic_trading_bot.event.global;

import com.bux.bot.basic_trading_bot.event.base.GenericObserver;
import com.bux.bot.basic_trading_bot.event.websocket.WebSocketEvent;

public interface GlobalEventObserver<T> extends GenericObserver<GlobalEvent<T>> {

}
