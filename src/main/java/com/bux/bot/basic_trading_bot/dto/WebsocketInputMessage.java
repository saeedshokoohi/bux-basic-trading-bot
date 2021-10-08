package com.bux.bot.basic_trading_bot.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class WebsocketInputMessage {
    public JsonNode body;
    public String t;
}


