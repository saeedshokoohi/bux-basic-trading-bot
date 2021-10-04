package com.bux.bot.basic_trading_bot.config;


import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BrokerConfiguration {
    private WebSocketConfiguration websocket;
    private RestConfiguration rest;

}
