package com.bux.bot.basic_trading_bot.client.rest.bux_impl.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Price{
    public String currency;
    public int decimals;
    public String amount;
}