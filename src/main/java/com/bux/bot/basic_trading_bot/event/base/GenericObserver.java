package com.bux.bot.basic_trading_bot.event.base;


public interface GenericObserver<T> {
    void next(T event );
}
