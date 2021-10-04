package com.bux.bot.basic_trading_bot.config;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix="brokers" , ignoreInvalidFields = false)
@Data
@NoArgsConstructor
public class BrokersConfiguration {
    private BrokerConfiguration bux;
}
