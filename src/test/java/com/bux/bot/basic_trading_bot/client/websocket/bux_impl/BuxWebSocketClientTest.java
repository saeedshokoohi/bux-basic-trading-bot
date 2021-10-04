package com.bux.bot.basic_trading_bot.client.websocket.bux_impl;

import com.bux.bot.basic_trading_bot.util.JsonUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
class BuxWebSocketClientTest {

    @Autowired
    BuxTrackerService buxTrackerService;

    @Test
    void contextLoads() throws InterruptedException {
    buxTrackerService
        .subscribeOnProductPrice("sb26500")
        .map(e -> {
            try {
                return JsonUtil.toJsonFormat(e);
            } catch (JsonProcessingException ex) {
                return e.toString();
            }
        }).log()
        .subscribe();
        buxTrackerService.subscribeOnProductPrice("sb26502").log().subscribe();
        Thread.sleep(5000);
        buxTrackerService.connect().blockFirst();
      //  buxTrackerService.subscribeOnProductPrice("sb26493").log().subscribe();

            Thread.sleep(500000);

    }

}