package com.bux.bot.basic_trading_bot.event.base;

import com.bux.bot.basic_trading_bot.event.global.GlobalEvent;

public interface GenericObserver<T> {
    void next(T event );
}
