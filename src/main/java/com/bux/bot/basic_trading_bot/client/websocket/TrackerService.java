package com.bux.bot.basic_trading_bot.client.websocket;

import com.bux.bot.basic_trading_bot.dto.ProductPrice;
import com.bux.bot.basic_trading_bot.dto.WebSocketEventMessage;
import reactor.core.publisher.Flux;

public interface TrackerService {
  Flux<WebSocketEventMessage> connect();

  void monitorProductPrice(String productId);

  Flux<ProductPrice> subscribeOnProductPrice(String productId);

  Flux<ProductPrice> subscribeOnAllProductPrice();

  void unsubscribeOnProductPrice(String productId);
}
