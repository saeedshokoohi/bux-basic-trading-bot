package com.bux.bot.basic_trading_bot.service;

import com.bux.bot.basic_trading_bot.client.websocket.TrackerService;
import com.bux.bot.basic_trading_bot.event.global.GlobalEventBus;
import com.bux.bot.basic_trading_bot.event.global.GlobalEventType;
import com.bux.bot.basic_trading_bot.exception.InvalidBodyRequestException;
import com.bux.bot.basic_trading_bot.exception.InvalidBrokerConfigurationException;
import com.bux.bot.basic_trading_bot.exception.WebClientInitializationException;
import com.bux.bot.basic_trading_bot.entity.BotOrderInfo;
import com.bux.bot.basic_trading_bot.entity.enums.BotOrderStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

@Service
public class StartupService {

  final BotOrderInfoService botOrderInfoService;
  final TrackerService trackerService;
  final BotEngineService botEngineService;
  final GlobalEventBus globalEventBus;

  public StartupService(
          BotOrderInfoService botOrderInfoService,
          TrackerService trackerService,
          BotEngineService botEngineService, GlobalEventBus globalEventBus) {
    this.botOrderInfoService = botOrderInfoService;
    this.trackerService = trackerService;
    this.botEngineService = botEngineService;
      this.globalEventBus = globalEventBus;

    this.startTradingBot();

  }

  public void startTradingBot() {
      //websocket connection achieved
    trackerService.connect();
    //getting current orders to track
    Flux<BotOrderInfo> botOrdersToTrack = getBotOrderInfoToTrack();
    botOrdersToTrack.subscribe(
        botOrder -> {
          trackerService.monitorProductPrice(botOrder.getProductId());
        });
    botOrdersToTrack.subscribe(
        botOrder -> {
          trackerService
              .subscribeOnProductPrice(botOrder.getProductId())
              .subscribe(productPrice -> {
                  try {
                      botEngineService.checkPrice(botOrder,productPrice);
                  } catch (WebClientInitializationException e) {
                      e.printStackTrace();
                  } catch (InvalidBrokerConfigurationException e) {
                      e.printStackTrace();
                  } catch (InvalidBodyRequestException e) {
                      e.printStackTrace();
                  }
              });
        });
    //listening on event bus to check changes on bot orders
      globalEventBus.subscribeOnEventType(GlobalEventType.BOTORDER_ADDED,event->{
          if(event.getPayload() instanceof BotOrderInfo)
          {
              BotOrderInfo botOrder = (BotOrderInfo) event.getPayload();
              trackerService.subscribeOnProductPrice(botOrder.getProductId()).subscribe(productPrice->{
                  try {
                      botEngineService.checkPrice(botOrder,productPrice);
                  } catch (WebClientInitializationException e) {
                      e.printStackTrace();
                  } catch (InvalidBrokerConfigurationException e) {
                      e.printStackTrace();
                  } catch (InvalidBodyRequestException e) {
                      e.printStackTrace();
                  }
              });


          }
      });
      globalEventBus.subscribeOnEventType(GlobalEventType.BOTORDER_CLOSED,event->{
          if(event.getPayload() instanceof BotOrderInfo)
          {
              BotOrderInfo botOrder = (BotOrderInfo) event.getPayload();
              trackerService.unsubscribeOnProductPrice(botOrder.getProductId());
          }
      });

  }

  private Flux<BotOrderInfo> getBotOrderInfoToTrack() {
    List<BotOrderStatus> underTrackStatuses = new ArrayList<>();
    underTrackStatuses.add(BotOrderStatus.ACTIVE);
    underTrackStatuses.add(BotOrderStatus.OPEN);
    return botOrderInfoService.findByStatuses(underTrackStatuses);
  }
}
