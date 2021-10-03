package com.bux.bot.basic_trading_bot.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtil {
    public static String toJsonFormat(Object obj)
    {
        if(obj==null)return "";
        ObjectMapper objectMapper = new ObjectMapper();
        try {
          return   objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "";
        }
    }
    public static <T> T jsonToObject(String content, Class<T> type) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
       return objectMapper.readValue(content,type);

    }
}
