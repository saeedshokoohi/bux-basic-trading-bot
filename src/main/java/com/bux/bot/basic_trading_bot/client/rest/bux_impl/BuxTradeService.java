package com.bux.bot.basic_trading_bot.client.rest.bux_impl;

import com.bux.bot.basic_trading_bot.client.rest.TradeClientService;
import com.bux.bot.basic_trading_bot.client.rest.bux_impl.dto.InvestingAmount;
import com.bux.bot.basic_trading_bot.client.rest.bux_impl.dto.OpenPositionRequest;
import com.bux.bot.basic_trading_bot.client.rest.bux_impl.dto.OpenPositionResponse;
import com.bux.bot.basic_trading_bot.client.rest.bux_impl.dto.PositionDirection;
import com.bux.bot.basic_trading_bot.exception.InvalidBrokerConfigurationException;
import com.bux.bot.basic_trading_bot.exception.WebClientInitializationException;
import com.sun.istack.NotNull;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class BuxTradeService implements TradeClientService {

    final BuxWebClientFactory buxWebClientFactory;

    public BuxTradeService( BuxWebClientFactory buxWebClientFactory) {
        this.buxWebClientFactory = buxWebClientFactory;
    }

    public Mono<OpenPositionResponse> openLongPosition(@NotNull String productId,@NotNull  String amount, int leverage, int decimals, String currency) throws WebClientInitializationException, InvalidBrokerConfigurationException {
        if(buxWebClientFactory==null) throw new WebClientInitializationException("buxWebClient could not be initialized");
        OpenPositionRequest openPositionRequest=createOpenPositionRequest(PositionDirection.BUY,productId,amount,leverage,decimals,currency);
        String uri="/users/me/trades";
      return  buxWebClientFactory.getWebClient().post()
                .uri(uri)
                .body(Mono.just(openPositionRequest),OpenPositionRequest.class)
                .retrieve()
                .bodyToMono(OpenPositionResponse.class);
    }

   private OpenPositionRequest  createOpenPositionRequest(PositionDirection direction, String productId, String amount, int leverage, int decimals, String currency)
    {
        OpenPositionRequest openPositionRequest=new OpenPositionRequest();
        openPositionRequest.setProductId(productId);
        openPositionRequest.setDirection(direction.getName());
        openPositionRequest.setLeverage(leverage);
        InvestingAmount investingAmount=new InvestingAmount(currency,decimals,amount);
        openPositionRequest.setInvestingAmount(investingAmount);
        return openPositionRequest;
    }







}
