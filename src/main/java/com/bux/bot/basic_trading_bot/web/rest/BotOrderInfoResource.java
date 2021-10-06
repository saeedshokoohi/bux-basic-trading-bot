package com.bux.bot.basic_trading_bot.web.rest;

import com.bux.bot.basic_trading_bot.entity.BotOrderInfo;
import com.bux.bot.basic_trading_bot.service.BotOrderInfoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController()
@RequestMapping("/api/bot-order")
public class BotOrderInfoResource {

    final BotOrderInfoService buxTrackerService;

    public BotOrderInfoResource(BotOrderInfoService buxTrackerService) {
        this.buxTrackerService = buxTrackerService;
    }

  @PostMapping("/add")
  public ResponseEntity<BotOrderInfo> add(BotOrderInfo botOrderInfo) {
return   buxTrackerService.addNewBotOrderInfo(botOrderInfo).
        map(e->new ResponseEntity<BotOrderInfo>(e, HttpStatus.OK))
        .onErrorResume(e->Mono.just(handleError(e)))
        .block();


    }



    private ResponseEntity<BotOrderInfo> handleError(Object throwable) {
        return ResponseEntity.badRequest().header("error",throwable.toString()).body(null);
    }


}
