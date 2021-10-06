package com.bux.bot.basic_trading_bot.event.global;

import com.sun.istack.NotNull;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class GlobalEventBus {
    Set<GlobalObserverTypeTuple> observers;

    public GlobalEventBus() {
        observers=new HashSet<>();
    }

    public boolean subscribe(@NotNull GlobalEventObserver observer) {
        return observers.add(new GlobalObserverTypeTuple(observer,null));
    }
    public boolean subscribeOnEventType(@NotNull GlobalEventObserver observer,@NotNull GlobalEventType type) {
        return observers.add(new GlobalObserverTypeTuple(observer,type));
    }
    public void emit(GlobalEvent event) {
        observers.forEach(observerTuple -> {
            if(observerTuple.getType() == null || observerTuple.getType().equals(event.getType())) {
                observerTuple.getObserver().next(event);
            }
            });
    }
}
