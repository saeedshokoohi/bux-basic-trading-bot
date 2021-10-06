package com.bux.bot.basic_trading_bot.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OpenPositionResponse {
    private String id;
    private String positionId;
    private Product product;
    private InvestingAmount investingAmount;
    private Price price;
    private int leverage;
    private String direction;
    private String type;
    private long dateCreated;
}



