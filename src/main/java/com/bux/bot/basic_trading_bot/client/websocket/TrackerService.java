package com.bux.bot.basic_trading_bot.client.websocket;

import com.bux.bot.basic_trading_bot.dto.ProductPrice;
import com.bux.bot.basic_trading_bot.dto.WebSocketEventMessage;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TrackerService {
  /***
   * try to connect to websocket server
   * it will
   * @return
   */
  Mono<Boolean> connect();

  /***
   * by calling this method you asking the web socket provider to subscribe on given product Id
   * @param productId
   */
  void monitorProductPrice(String productId);

  /***
   * listing to the product price stream that are emmited from the websocket session
   * @return
   */
  Flux<ProductPrice> subscribeOnAllProductPrice();
  /***
   * listing to the specific product price stream that are emmited from the websocket session
   * @param productId
   * @return
   */
  Flux<ProductPrice> subscribeOnProductPrice(String productId);

  /***
   * asking the websocket service that send unsubscribe message for specific product
   * @param productId
   */
  void unsubscribeOnProductPrice(String productId);
}
