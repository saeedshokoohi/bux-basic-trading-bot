package com.bux.bot.basic_trading_bot;

import com.bux.bot.basic_trading_bot.config.BrokersConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
@Configuration
@EnableConfigurationProperties({BrokersConfiguration.class})
public class BasicTradingBotApplication {
    //start point of app
	public static void main(String[] args) {
		SpringApplication.run(BasicTradingBotApplication.class, args);
	}

}
