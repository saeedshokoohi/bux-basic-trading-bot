package com.bux.bot.basic_trading_bot.config;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
@ConfigurationProperties(prefix="app" , ignoreInvalidFields = false)
@Data
@NoArgsConstructor
@Component
public class ApplicationConfiguration {
    private  boolean autoStart=false;
}
