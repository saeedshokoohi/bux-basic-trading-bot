package com.bux.bot.basic_trading_bot.event.global;

import javax.validation.constraints.NotNull;

public class GlobalObserverTypeTuple<T> {

  private GlobalEventObserver<T> observer;
  private GlobalEventType type;

  public GlobalObserverTypeTuple(@NotNull GlobalEventObserver<T> observer, GlobalEventType type) {
    this.observer = observer;
    this.type = type;
  }

  public GlobalEventObserver<T> getObserver() {
    return observer;
  }

  public void setObserver(GlobalEventObserver<T> observer) {
    this.observer = observer;
  }

  public GlobalEventType getType() {
    return type;
  }

  public void setType(GlobalEventType type) {
    this.type = type;
  }
}
