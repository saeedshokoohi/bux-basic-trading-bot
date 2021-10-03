package com.bux.bot.basic_trading_bot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
@Configuration
public class BasicTradingBotApplication {
    //start point of app
	public static void main(String[] args) {
		SpringApplication.run(BasicTradingBotApplication.class, args);
	}

}
