package com.bux.bot.basic_trading_bot.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product{
    public String securityId;
    public String symbol;
    public String displayName;
}