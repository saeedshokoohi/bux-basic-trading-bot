package com.bux.bot.basic_trading_bot.dto;

import lombok.Data;

@Data
public class WebsocketInputMessage {
    public WebsocketMessageBody websocketMessageBody;
    public String t;
}


