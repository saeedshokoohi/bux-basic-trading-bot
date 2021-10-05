package com.bux.bot.basic_trading_bot.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ErrorResponse {
    public String message;
    public String developerMessage;
    public String errorCode;
}
