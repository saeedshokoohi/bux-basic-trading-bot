package com.bux.bot.basic_trading_bot.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtil {
    public static String toJsonFormat(Object obj) throws JsonProcessingException {
        if(obj==null)return "";
        ObjectMapper objectMapper = new ObjectMapper();

          return   objectMapper.writeValueAsString(obj);

    }
    public static <T> T jsonToObject(String content, Class<T> type) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
       return objectMapper.readValue(content,type);

    }
}
