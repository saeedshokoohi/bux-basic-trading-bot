package com.bux.bot.basic_trading_bot.repository;

import com.bux.bot.basic_trading_bot.entity.BotOrderInfo;
import com.bux.bot.basic_trading_bot.entity.enums.BotOrderStatus;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BotOrderInfoRepository extends CrudRepository<BotOrderInfo, Long> {
    List<BotOrderInfo> findByStatus(BotOrderStatus status);
    List<BotOrderInfo> findByStatusIn(List<BotOrderStatus> statuses);
}
