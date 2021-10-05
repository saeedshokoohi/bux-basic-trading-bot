package com.bux.bot.basic_trading_bot.repository;

import com.bux.bot.basic_trading_bot.model.BotOrderInfo;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BotOrderInfoRepository extends ReactiveCrudRepository<BotOrderInfo, Long> {

}
