package com.bux.bot.basic_trading_bot.config;

import lombok.Data;

@Data
public class WebSocketConfiguration {
    private String baseUrl;
    private String channelUrl;
    private String accessToken;

}
