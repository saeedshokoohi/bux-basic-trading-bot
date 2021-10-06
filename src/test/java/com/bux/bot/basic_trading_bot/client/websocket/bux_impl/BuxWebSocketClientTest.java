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
        .monitorProductPrice("sb26500");

        buxTrackerService.monitorProductPrice("sb26502");
        Thread.sleep(5000);
        buxTrackerService.connect().blockFirst();
      //  buxTrackerService.subscribeOnProductPrice("sb26493").log().subscribe();

            Thread.sleep(500000);

    }

}