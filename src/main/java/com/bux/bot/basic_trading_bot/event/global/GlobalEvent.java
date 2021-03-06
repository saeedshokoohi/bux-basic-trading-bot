package com.bux.bot.basic_trading_bot.event.global;



public class GlobalEvent<T> {
    private GlobalEventType type;
    private Object source;
    private T payload;

    public GlobalEvent(GlobalEventType type, Object source, T payload) {
        this.type = type;
        this.source = source;
        this.payload = payload;
    }

    public GlobalEventType getType() {
        return type;
    }

    public void setType(GlobalEventType type) {
        this.type = type;
    }

    public Object getSource() {
        return source;
    }

    public void setSource(Object source) {
        this.source = source;
    }

    public T getPayload() {
        return payload;
    }

    public void setPayload(T payload) {
        this.payload = payload;
    }
}
