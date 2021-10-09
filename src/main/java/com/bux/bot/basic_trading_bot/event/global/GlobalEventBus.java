package com.bux.bot.basic_trading_bot.event.global;


import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

@Service
public class GlobalEventBus {
    Set<GlobalObserverTypeTuple> observers;

    public GlobalEventBus() {
        observers=new HashSet<>();
    }

    public <T> boolean subscribe(@NotNull GlobalEventObserver<T> observer) {
        return observers.add(new GlobalObserverTypeTuple(observer,null));
    }
    public <T> boolean subscribeOnEventType(@NotNull GlobalEventType type,@NotNull GlobalEventObserver<T> observer) {
        return observers.add(new GlobalObserverTypeTuple(observer,type));
    }
    public <T> void emit(GlobalEvent<T> event) {
        observers.forEach(observerTuple -> {
            if(observerTuple.getType() == null || observerTuple.getType().equals(event.getType())) {
                observerTuple.getObserver().next(event);
            }
            });
    }
}
