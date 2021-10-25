package com.bux.bot.basic_trading_bot.service;

import com.bux.bot.basic_trading_bot.client.websocket.TrackerService;
import com.bux.bot.basic_trading_bot.config.ApplicationConfiguration;
import com.bux.bot.basic_trading_bot.dto.ProductPrice;
import com.bux.bot.basic_trading_bot.entity.BotOrderInfo;
import com.bux.bot.basic_trading_bot.entity.enums.BotOrderStatus;
import com.bux.bot.basic_trading_bot.event.global.GlobalEventBus;
import com.bux.bot.basic_trading_bot.event.global.GlobalEventType;
import com.bux.bot.basic_trading_bot.exception.InvalidBodyRequestException;
import com.bux.bot.basic_trading_bot.exception.InvalidBrokerConfigurationException;
import com.bux.bot.basic_trading_bot.exception.WebClientInitializationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

import static com.bux.bot.basic_trading_bot.entity.enums.BotOrderStatus.ACTIVE;
import static com.bux.bot.basic_trading_bot.entity.enums.BotOrderStatus.OPEN;

@Service
public class StartupService {
    Logger logger = LoggerFactory.getLogger(StartupService.class);

    final ApplicationConfiguration applicationConfiguration;
    final BotOrderInfoService botOrderInfoService;
    final TrackerService trackerService;
    final BotEngineService botEngineService;
    final GlobalEventBus globalEventBus;

  public StartupService(
      BotOrderInfoService botOrderInfoService,
      TrackerService trackerService,
      BotEngineService botEngineService,
      GlobalEventBus globalEventBus,
      ApplicationConfiguration applicationConfiguration) {
    this.botOrderInfoService = botOrderInfoService;
    this.trackerService = trackerService;
    this.botEngineService = botEngineService;
    this.globalEventBus = globalEventBus;
    this.applicationConfiguration = applicationConfiguration;

    if (this.applicationConfiguration.isAutoStart()) this.startTradingBot();
  }

  public void startTradingBot() {
    logger.info("Starting trading bot engine...");
    // websocket connection achieved
    trackerService
        .connect()
        .subscribe(
            e -> {
              // getting current orders to track
              monitoringCurrentActiveAndOpenOrders();

              // listening on event bus to check changes on bot orders
              checkingOnRemovedOrClosedBotOrder();
            });
  }

  /***
   * this method handles the BOTORDER_CLOSED and BOTORDER_REMOVED events
   */
  private void checkingOnRemovedOrClosedBotOrder() {
    globalEventBus.subscribeOnEventType(
        GlobalEventType.BOTORDER_CLOSED,
        event -> {
          if (event.getPayload() instanceof BotOrderInfo) {
            BotOrderInfo botOrder = (BotOrderInfo) event.getPayload();

            trackerService.unsubscribeOnProductPrice(botOrder.getProductId());
          }
        });
    globalEventBus.subscribeOnEventType(
        GlobalEventType.BOTORDER_REMOVED,
        event -> {
          if (event.getPayload() instanceof BotOrderInfo) {
            BotOrderInfo botOrder = (BotOrderInfo) event.getPayload();
            if (ACTIVE.equals(botOrder.getStatus()) || OPEN.equals(botOrder.getStatus()))
              trackerService.unsubscribeOnProductPrice(botOrder.getProductId());
          }
        });
  }

  /***
   * this methods check on ACTIVE and Open BotOrders
   */
  private void monitoringCurrentActiveAndOpenOrders() {
    Flux<BotOrderInfo> botOrdersToTrack = getBotOrderInfoToTrack();
    Flux<ProductPrice> priceStream =
        trackerService
            .subscribeOnAllProductPrice();
    // telling the tracker service that track the given products
    botOrdersToTrack.subscribe(
        botOrder -> trackerService.monitorProductPrice(botOrder.getProductId()));
    //
    botOrdersToTrack
        .doOnNext(
            botOrder -> {
              priceStream
                  .filter(
                      price ->
                          (botOrder != null
                              && price != null
                              && price.getSecurityId().equals(botOrder.getProductId())))
                  .flatMap(
                      price -> {
                          try {
                              return botEngineService.checkPrice(botOrder, price);
                          } catch (WebClientInitializationException e) {
                            return  Mono.error(e);
                          } catch (InvalidBrokerConfigurationException e) {
                              return  Mono.error(e);
                          } catch (InvalidBodyRequestException e) {
                              return  Mono.error(e);
                          }
                      })
                  .doOnNext(mix -> System.out.println(mix))
                  .subscribe();
            })
        .subscribe();
//    botOrdersToTrack.subscribe(
//        botOrder -> {
//          trackerService
//              .subscribeOnProductPrice(botOrder.getProductId())
//              .subscribe(
//                  productPrice -> {
//                    try {
//                      botEngineService.checkPrice(botOrder, productPrice);
//                    } catch (WebClientInitializationException e) {
//                      logger.error("exception on checking price ", e);
//                    } catch (InvalidBrokerConfigurationException e) {
//                      logger.error("exception on checking price ", e);
//                    } catch (InvalidBodyRequestException e) {
//                      logger.error("exception on checking price ", e);
//                    }
//                  });
//        });
  }

  /***
   * getting active and open trades
   * @return
   */
  private Flux<BotOrderInfo> getBotOrderInfoToTrack() {
    List<BotOrderStatus> underTrackStatuses = new ArrayList<>();
    underTrackStatuses.add(ACTIVE);
    underTrackStatuses.add(BotOrderStatus.OPEN);
    return botOrderInfoService.findByStatuses(underTrackStatuses);
  }
}
