package com.bux.bot.basic_trading_bot.service;

import com.bux.bot.basic_trading_bot.client.websocket.TrackerService;
import com.bux.bot.basic_trading_bot.model.BotOrderInfo;
import com.bux.bot.basic_trading_bot.model.enums.BotOrderStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

@Service
public class StartupService {

  final BotOrderInfoService botOrderInfoService;
  final TrackerService trackerService;
  final BotEngineService botEngineService;

  public StartupService(
      BotOrderInfoService botOrderInfoService,
      TrackerService trackerService,
      BotEngineService botEngineService) {
    this.botOrderInfoService = botOrderInfoService;
    this.trackerService = trackerService;
    this.botEngineService = botEngineService;
  }

  public void start() {
    trackerService.connect();
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
                botEngineService.checkPrice(botOrder,productPrice);
              });
        });
  }

  private Flux<BotOrderInfo> getBotOrderInfoToTrack() {
    List<BotOrderStatus> underTrackStatuses = new ArrayList<>();
    underTrackStatuses.add(BotOrderStatus.ACTIVE);
    underTrackStatuses.add(BotOrderStatus.OPEN);
    return botOrderInfoService.findByStatuses(underTrackStatuses);
  }
}
