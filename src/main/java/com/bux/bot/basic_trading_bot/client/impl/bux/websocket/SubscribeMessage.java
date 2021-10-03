package com.bux.bot.basic_trading_bot.client.impl.bux.websocket;

import com.bux.bot.basic_trading_bot.util.JsonUtil;

public class SubscribeMessage {
    private String[] subscribeTo;

    public String[] getSubscribeTo() {
        return subscribeTo;
    }

    public void setSubscribeTo(String[] subscribeTo) {
        this.subscribeTo = subscribeTo;
    }

    public SubscribeMessage(String[] subscribeTo) {
        this.subscribeTo = subscribeTo;
    }
    public SubscribeMessage(String subscribeTo) {
        this.subscribeTo = new String[1];
        this.subscribeTo[0]=subscribeTo;
    }
    @Override
    public String toString() {
       return JsonUtil.toJsonFormat(this);
    }
}
