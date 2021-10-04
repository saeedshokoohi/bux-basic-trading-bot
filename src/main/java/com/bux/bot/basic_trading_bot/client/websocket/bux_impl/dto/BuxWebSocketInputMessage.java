package com.bux.bot.basic_trading_bot.client.websocket.bux_impl.dto;

import lombok.Data;

@Data
public class BuxWebSocketInputMessage {
    public Body body;
    public String t;
}
@Data
 class Body{
    public String userId;
    public String sessionId;
    public long time;
    public String securityId;
    public String currentPrice;
    public long timeStamp;
}
