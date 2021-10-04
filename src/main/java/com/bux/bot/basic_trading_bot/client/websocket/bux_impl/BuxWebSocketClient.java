package com.bux.bot.basic_trading_bot.client.websocket.bux_impl;

import com.bux.bot.basic_trading_bot.config.BrokerConfiguration;
import com.bux.bot.basic_trading_bot.config.BrokersConfiguration;
import com.bux.bot.basic_trading_bot.exception.InvalidBrokerConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
    final BrokersConfiguration brokersConfiguration;
    private  String baseUrl;
    private String channelUrl;
    private String accessToken;

    public BuxWebSocketClient(BuxWebSocketHandler buxWebSocketHandler, BrokersConfiguration brokersConfiguration) throws InvalidBrokerConfigurationException {
        this.buxWebSocketHandler = buxWebSocketHandler;
        this.brokersConfiguration = brokersConfiguration;
        initFromConfiguration();

    }



    public Mono<Void> getConnection()
    {
       // String url="https://rtf.beta.getbux.com/subscriptions/me";
       // String url="http://localhost:8080/subscriptions/me";
        String url=String.format("%s%s",baseUrl,channelUrl);
        HttpHeaders headers=new HttpHeaders();
        headers.add("Accept-Language","posnl-NL,en;q=0.8");
        headers.add("Authorization","Bearer " +accessToken);
        WebSocketClient client = new ReactorNettyWebSocketClient();
        return client
                .execute(URI.create(url),headers, buxWebSocketHandler);
    }
    private void initFromConfiguration() throws InvalidBrokerConfigurationException {
        //handling exception
        if(brokersConfiguration==null )throw new InvalidBrokerConfigurationException("broker configuration not set");
        if(brokersConfiguration.getBux()==null)throw new InvalidBrokerConfigurationException("broker.bux configuration not set");
        if(brokersConfiguration.getBux().getWebsocket()==null)throw new InvalidBrokerConfigurationException("broker.bux.websocket configuration not set");
        if(brokersConfiguration.getBux().getWebsocket().getBaseUrl()==null)throw new InvalidBrokerConfigurationException("broker.bux.websocket.baseUrl configuration not set");
        if(brokersConfiguration.getBux().getWebsocket().getChannelUrl()==null)throw new InvalidBrokerConfigurationException("broker.bux.websocket.channelUrl configuration not set");
        if(brokersConfiguration.getBux().getWebsocket().getAccessToken()==null)throw new InvalidBrokerConfigurationException("broker.bux.websocket.accessToken configuration not set");
        //setting variables
        baseUrl=brokersConfiguration.getBux().getWebsocket().getBaseUrl();
        channelUrl=brokersConfiguration.getBux().getWebsocket().getChannelUrl();
        accessToken=brokersConfiguration.getBux().getWebsocket().getAccessToken();

    }
}
