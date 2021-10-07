package com.bux.bot.basic_trading_bot.web.rest;

import com.bux.bot.basic_trading_bot.entity.BotOrderInfo;
import com.bux.bot.basic_trading_bot.exception.EntityValidationException;
import com.bux.bot.basic_trading_bot.service.BotOrderInfoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
  public ResponseEntity<String> add(@RequestBody BotOrderInfo botOrderInfo) {
return   buxTrackerService.addNewBotOrderInfo(botOrderInfo).
        map(e->new ResponseEntity<>(e.getId().toString(), HttpStatus.OK))
        .onErrorResume(e->Mono.just(handleError(e)))
        .block();


    }



    private ResponseEntity<String> handleError(Object exception) {
        if(exception instanceof EntityValidationException) {
                return ResponseEntity.badRequest().body(exception.toString());
        }

            return ResponseEntity.badRequest().body(((Exception) exception).getMessage());

    }


}
