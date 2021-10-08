package com.bux.bot.basic_trading_bot.web.rest;

import com.bux.bot.basic_trading_bot.entity.BotOrderInfo;
import com.bux.bot.basic_trading_bot.exception.EntityValidationException;
import com.bux.bot.basic_trading_bot.service.BotOrderInfoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController()
@RequestMapping("/api/bot-order")
public class BotOrderInfoResource {

    final BotOrderInfoService botOrderInfoService;

    public BotOrderInfoResource(BotOrderInfoService buxTrackerService) {
        this.botOrderInfoService = buxTrackerService;
    }

  @PostMapping("/add")
  public ResponseEntity<String> add(@RequestBody BotOrderInfo botOrderInfo) {
return   botOrderInfoService.addNewBotOrderInfo(botOrderInfo).
        map(e->new ResponseEntity<>(e.getId().toString(), HttpStatus.OK))
        .onErrorResume(e->Mono.just(handleError(e)))
        .block();


    }

    @GetMapping("/clear")
    public ResponseEntity<String> clear() {
        return   botOrderInfoService.clear().
                map(e->new ResponseEntity<>(e+" records deleted.", HttpStatus.OK))
                .onErrorResume(e->Mono.just(handleError(e)))
                .block();


    }
    @GetMapping("/list")
    public ResponseEntity<List<BotOrderInfo>> list()
    {
        return   botOrderInfoService.findAll().
                map(e->new ResponseEntity<>(e, HttpStatus.OK))
                .block();
    }


    private ResponseEntity<String> handleError(Object exception) {
        if(exception instanceof EntityValidationException) {
                return ResponseEntity.badRequest().body(exception.toString());
        }

            return ResponseEntity.badRequest().body(((Exception) exception).getMessage());

    }


}
