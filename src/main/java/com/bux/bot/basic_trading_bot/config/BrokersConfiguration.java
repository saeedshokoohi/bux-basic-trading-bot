package com.bux.bot.basic_trading_bot.config;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix="brokers" , ignoreInvalidFields = true)
@Data
@NoArgsConstructor
public class BrokersConfiguration {
    private BrokerConfiguration bux;
}
