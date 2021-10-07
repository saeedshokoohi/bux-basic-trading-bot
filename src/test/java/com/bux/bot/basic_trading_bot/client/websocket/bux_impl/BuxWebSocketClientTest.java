package com.bux.bot.basic_trading_bot.client.websocket.bux_impl;

import com.bux.bot.basic_trading_bot.client.rest.bux_impl.BuxTradeService;
import com.bux.bot.basic_trading_bot.client.rest.bux_impl.BuxWebClientFactory;
import com.bux.bot.basic_trading_bot.config.BrokersConfiguration;
import com.bux.bot.basic_trading_bot.exception.InvalidBrokerConfigurationException;
import com.bux.bot.basic_trading_bot.util.JsonUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@EnableConfigurationProperties(value = BrokersConfiguration.class)
@ContextConfiguration(classes = {BrokersConfiguration.class})
@ExtendWith(SpringExtension.class)
@SpringBootTest
class BuxWebSocketClientTest {
    MockWebServer mockBackEnd;
    @Autowired BrokersConfiguration brokersConfiguration;
    @MockBean
     BuxWebSocketHandler buxWebSocketHandler;


    BuxWebSocketClient buxWebSocketClient;

    @BeforeEach
    void initialize() throws InvalidBrokerConfigurationException, IOException {
        mockBackEnd = new MockWebServer();
        mockBackEnd.start();
        String baseUrl = String.format("http://localhost:%s", mockBackEnd.getPort());
        this.brokersConfiguration.getBux().getWebsocket().setBaseUrl(baseUrl);
        buxWebSocketClient=new BuxWebSocketClient(buxWebSocketHandler, this.brokersConfiguration);
    }

    @AfterEach
    void dispose() throws IOException {
      //  mockBackEnd.shutdown();
    }

    @Test
    void getConnectionTest() throws InterruptedException {

        //given
        this.initMockServer();
        when(this.buxWebSocketHandler.handle(any())).thenReturn(Mono.empty());

        //when
        this.buxWebSocketClient.getConnection().block();

        //then
        verify(this.buxWebSocketHandler).handle(any());


    }
    private void initMockServer() {
        this.mockBackEnd.enqueue(new MockResponse().withWebSocketUpgrade(new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                System.out.println("server onOpen");
                System.out.println("server request header:" + response.request().headers());
                System.out.println("server response header:" + response.headers());
                System.out.println("server response:" + response);
            }

            @Override
            public void onMessage(WebSocket webSocket, String string) {
                System.out.println("server onMessage");
                System.out.println("message:" + string);

                webSocket.send("response-" + string);
            }

            @Override
            public void onClosing(WebSocket webSocket, int code, String reason) {
                System.out.println("server onClosing");
                System.out.println("code:" + code + " reason:" + reason);
            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                System.out.println("server onClosed");
                System.out.println("code:" + code + " reason:" + reason);
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                System.out.println("server onFailure");
                System.out.println("throwable:" + t);
                System.out.println("response:" + response);
            }

        }));
    }


}