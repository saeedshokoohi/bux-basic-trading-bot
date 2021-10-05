package com.bux.bot.basic_trading_bot.dto.enums;

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
