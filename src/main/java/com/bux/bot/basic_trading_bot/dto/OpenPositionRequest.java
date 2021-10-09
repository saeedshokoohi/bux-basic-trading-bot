package com.bux.bot.basic_trading_bot.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenPositionRequest {

  private String productId;
  private InvestingAmount investingAmount;
  private int leverage;
  private String direction;
  private Source source = new Source("OTHER");
  private String riskWarningConfirmation;

  @Data
  @NoArgsConstructor
  class Source {
    private String sourceType;
    private String sourceId;

    public Source(String sourceType) {
      this.sourceType = sourceType;
    }
  }
}
