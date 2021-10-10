package com.bux.bot.basic_trading_bot;

import com.bux.bot.basic_trading_bot.config.ApplicationConfiguration;
import com.bux.bot.basic_trading_bot.config.BrokersConfiguration;
import com.bux.bot.basic_trading_bot.service.StartupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@Configuration
@EnableConfigurationProperties({BrokersConfiguration.class, ApplicationConfiguration.class})
@EnableAsync
public class BasicTradingBotApplication {

	public static void main(String[] args) {
		SpringApplication.run(BasicTradingBotApplication.class, args);

	}

}
