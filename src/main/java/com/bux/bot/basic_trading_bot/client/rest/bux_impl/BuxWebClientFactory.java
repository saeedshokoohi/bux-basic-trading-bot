package com.bux.bot.basic_trading_bot.client.rest.bux_impl;

import com.bux.bot.basic_trading_bot.config.BrokersConfiguration;
import com.bux.bot.basic_trading_bot.exception.InvalidBrokerConfigurationException;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.TcpClient;

@Component
public class BuxWebClientFactory {
    final BrokersConfiguration brokersConfiguration;
    private  String baseUrl;
    private String accessToken;
    private WebClient webClient;

    public BuxWebClientFactory(BrokersConfiguration brokersConfiguration) throws InvalidBrokerConfigurationException {
        this.brokersConfiguration = brokersConfiguration;

    }

    public WebClient getWebClient() throws InvalidBrokerConfigurationException {
        if(webClient==null)
        {
            initFromConfiguration();
          webClient=buxWebClient();
        }
        return webClient;
    }
    public WebClient buxWebClient() {

        var tcpClient = TcpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 2_000)
                .doOnConnected(connection ->
                        connection.addHandlerLast(new ReadTimeoutHandler(10))
                                .addHandlerLast(new WriteTimeoutHandler(10)));

        return WebClient.builder()
                .baseUrl(baseUrl)
                .clientConnector(new ReactorClientHttpConnector(HttpClient.from(tcpClient)))
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT_LANGUAGE, "nl-NL,en;q=0.8")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer "+accessToken)
                .build();
    }
    private void initFromConfiguration() throws InvalidBrokerConfigurationException {
        //handling exception
        if(brokersConfiguration==null )throw new InvalidBrokerConfigurationException("broker configuration not set");
        if(brokersConfiguration.getBux()==null)throw new InvalidBrokerConfigurationException("broker.bux configuration not set");
        if(brokersConfiguration.getBux().getRest()==null)throw new InvalidBrokerConfigurationException("broker.bux.rest configuration not set");
        if(brokersConfiguration.getBux().getRest().getBaseUrl()==null)throw new InvalidBrokerConfigurationException("broker.bux.rest.baseUrl configuration not set");
        if(brokersConfiguration.getBux().getRest().getAccessToken()==null)throw new InvalidBrokerConfigurationException("broker.bux.rest.accessToken configuration not set");
        //setting variables
        String version=brokersConfiguration.getBux().getRest().getVersion();
        String env=brokersConfiguration.getBux().getRest().getEnv();
        baseUrl=brokersConfiguration.getBux().getRest().getBaseUrl().replace("{env}",env).replace("{version}",version);
        accessToken=brokersConfiguration.getBux().getRest().getAccessToken();

    }

}
