package com.bux.bot.basic_trading_bot.config;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BrokerConfiguration {
    private WebSocketConfiguration websocket;
    private RestConfiguration rest;

}
