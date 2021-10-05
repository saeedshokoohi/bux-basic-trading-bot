package com.bux.bot.basic_trading_bot.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvestingAmount{
    private String currency;
    private int decimals;
    private String amount;
}
