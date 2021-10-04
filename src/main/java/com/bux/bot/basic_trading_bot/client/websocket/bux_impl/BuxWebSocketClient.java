package com.bux.bot.basic_trading_bot.client.websocket.bux_impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;
import reactor.core.publisher.Mono;

import java.net.URI;

@Service
public class BuxWebSocketClient {

    Logger logger = LoggerFactory.getLogger(BuxWebSocketClient.class);
    private BuxWebSocketHandler buxWebSocketHandler;

    public BuxWebSocketClient(BuxWebSocketHandler buxWebSocketHandler) {
        this.buxWebSocketHandler = buxWebSocketHandler;
    }
    public Mono<Void> getConnection()
    {
       // String url="https://rtf.beta.getbux.com/subscriptions/me";
        String url="http://localhost:8080/subscriptions/me";
        HttpHeaders headers=new HttpHeaders();
        headers.add("Accept-Language","posnl-NL,en;q=0.8");
        headers.add("Authorization","Bearer " +
                "eyJhbGciOiJIUzI1NiJ9.eyJyZWZyZXNoYWJsZSI6ZmFsc2UsInN1YiI6ImJiMGNkYTJiLWE" +
                "xMGUtNGVkMy1hZDVhLTBmODJiNGMxNTJjNCIsImF1ZCI6ImJldGEuZ2V0YnV4LmNvbSIsInN" +
                "jcCI6WyJhcHA6bG9naW4iLCJydGY6bG9naW4iXSwiZXhwIjoxODIwODQ5Mjc5LCJpYXQiOjE" +
                "1MDU0ODkyNzksImp0aSI6ImI3MzlmYjgwLTM1NzUtNGIwMS04NzUxLTMzZDFhNGRjOGY5MiI" +
                "sImNpZCI6Ijg0NzM2MjI5MzkifQ.M5oANIi2nBtSfIfhyUMqJnex-JYg6Sm92KPYaUL9GKg");
        WebSocketClient client = new ReactorNettyWebSocketClient();
        return client
                .execute(URI.create(url),headers, buxWebSocketHandler);
    }
}
