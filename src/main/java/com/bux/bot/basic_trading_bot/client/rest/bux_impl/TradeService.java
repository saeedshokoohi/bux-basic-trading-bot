package com.bux.bot.basic_trading_bot.client.rest.bux_impl;

import com.bux.bot.basic_trading_bot.dto.ClosePositionResponse;
import com.bux.bot.basic_trading_bot.dto.OpenPositionResponse;
import com.bux.bot.basic_trading_bot.exception.InvalidBodyRequestException;
import com.bux.bot.basic_trading_bot.exception.InvalidBrokerConfigurationException;
import com.bux.bot.basic_trading_bot.exception.WebClientInitializationException;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;

public interface TradeService {
    Mono<OpenPositionResponse> openLongPosition(
            @NotNull String productId,
            @NotNull String amount,
            int leverage,
            int decimals,
            String currency)
            throws WebClientInitializationException, InvalidBrokerConfigurationException,
            InvalidBodyRequestException;

    Mono<ClosePositionResponse> closePosition(@NotNull String positionId)
            throws WebClientInitializationException, InvalidBrokerConfigurationException;
}
