package com.bux.bot.basic_trading_bot.client.rest.bux_impl.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ErrorResponse {
    public String message;
    public String developerMessage;
    public String errorCode;
}
