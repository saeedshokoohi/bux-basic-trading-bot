package com.bux.bot.basic_trading_bot.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OpenPositionResponse {
    public String id;
    public String positionId;
    public Product product;
    public InvestingAmount investingAmount;
    public Price price;
    public int leverage;
    public String direction;
    public String type;
    public long dateCreated;
}



