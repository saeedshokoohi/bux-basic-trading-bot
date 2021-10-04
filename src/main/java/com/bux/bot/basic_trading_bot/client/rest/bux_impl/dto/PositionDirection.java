package com.bux.bot.basic_trading_bot.client.rest.bux_impl.dto;

public enum PositionDirection {
    BUY("BUY"),SELL("SELL");
    private String name;

    public String getName() {
        return name;
    }

    private PositionDirection(String name)
    {
        this.name=name;
    }
}
