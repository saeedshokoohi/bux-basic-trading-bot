package com.bux.bot.basic_trading_bot.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
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



