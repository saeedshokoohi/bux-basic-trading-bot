package com.bux.bot.basic_trading_bot.client.rest.bux_impl.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvestingAmount{
    public String currency;
    public int decimals;
    public String amount;
}
