package com.bux.bot.basic_trading_bot.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Price{
    public String currency;
    public int decimals;
    public String amount;
}