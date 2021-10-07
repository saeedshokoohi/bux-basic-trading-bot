package com.bux.bot.basic_trading_bot.dto;

import com.bux.bot.basic_trading_bot.util.JsonUtil;
import com.fasterxml.jackson.core.JsonProcessingException;

public class UnSubscribeMessage {
    private String[] unsubscribeTo;

    public String[] getUnsubscribeTo() {
        return unsubscribeTo;
    }

    public void setUnsubscribeTo(String[] unsubscribeTo) {
        this.unsubscribeTo = unsubscribeTo;
    }

    public UnSubscribeMessage(String[] subscribeTo) {
        this.unsubscribeTo = subscribeTo;
    }
    public UnSubscribeMessage(String subscribeTo) {
        this.unsubscribeTo = new String[1];
        this.unsubscribeTo[0]=subscribeTo;
    }
    @Override
    public String toString() {
        try {
            return JsonUtil.toJsonFormat(this);
        } catch (JsonProcessingException e) {
           return super.toString();
        }
    }
}
