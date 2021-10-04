package com.bux.bot.basic_trading_bot.config;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RestConfiguration {
    private String baseUrl;
    private String channelUrl;
    private String accessToken;
    private String version="21";
    private String env="beta";
}
