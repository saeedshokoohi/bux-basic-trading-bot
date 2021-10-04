package com.bux.bot.basic_trading_bot.client.rest.bux_impl.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ClosePositionRequest {
    public String id;
    public String positionId;
    public ProfitAndLoss profitAndLoss;
    public Product product;
    public InvestingAmount investingAmount;
    public Price price;
    public int leverage;
    public String direction;
    public String type;
    public long dateCreated;
}







