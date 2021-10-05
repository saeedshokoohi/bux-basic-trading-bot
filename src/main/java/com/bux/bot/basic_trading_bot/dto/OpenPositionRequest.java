package com.bux.bot.basic_trading_bot.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class OpenPositionRequest {

        private String productId;
    private InvestingAmount investingAmount;
    private int leverage;
    private String direction;
    private Source source=new Source("OTHER");
    private String riskWarningConfirmation;


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


