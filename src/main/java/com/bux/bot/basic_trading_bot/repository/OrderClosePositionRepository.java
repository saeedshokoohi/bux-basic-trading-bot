package com.bux.bot.basic_trading_bot.repository;

import com.bux.bot.basic_trading_bot.entity.OrderClosePosition;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderClosePositionRepository extends CrudRepository<OrderClosePosition, Long> {}
