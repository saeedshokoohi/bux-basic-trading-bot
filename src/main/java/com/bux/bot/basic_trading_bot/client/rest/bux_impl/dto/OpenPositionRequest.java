package com.bux.bot.basic_trading_bot.client.rest.bux_impl.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class OpenPositionRequest {

        public String productId;
        public InvestingAmount investingAmount;
        public int leverage;
        public String direction;
        public Source source=new Source("OTHER");
        public String riskWarningConfirmation;


    @Data
    @NoArgsConstructor
    class Source{
        public String sourceType;
        public String sourceId;

        public Source(String sourceType) {
            this.sourceType = sourceType;
        }
    }
}


