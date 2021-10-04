package com.bux.bot.basic_trading_bot.client.rest.bux_impl.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OpenPositionRequest {
    public String productId;
    public InvestingAmount investingAmount;
    public int leverage;
    public String direction;
    public Source source;

    @Data
    @NoArgsConstructor
    class Source{
        public String sourceType;
    }
}


