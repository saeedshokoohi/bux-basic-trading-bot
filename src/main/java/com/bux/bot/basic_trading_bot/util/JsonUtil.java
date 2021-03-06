package com.bux.bot.basic_trading_bot.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class JsonUtil {
  private JsonUtil() {
  }

  static ObjectMapper objectMapper = new ObjectMapper();

  public static String toJsonFormat(Object obj) throws JsonProcessingException {
    if (obj == null) return "";

    return objectMapper.writeValueAsString(obj);
  }

  public static <T> T jsonToObject(String content, Class<T> type) throws JsonProcessingException {
    return objectMapper.readValue(content, type);
  }

  public static String getFieldValue(String jsonStr, String field) {
    String retValueAsString = null;
    final ObjectNode node;
    try {
      node = new ObjectMapper().readValue(jsonStr, ObjectNode.class);
      if (node.has(field)) {
        retValueAsString = node.get(field).asText();
      }
    } catch (JsonProcessingException e) {
      e.printStackTrace();

    }
    return retValueAsString;
  }
  public static JsonNode getFieldValueAsJsonNode(String jsonStr, String field) {
    JsonNode node  = null;

    try {
      node = new ObjectMapper().readValue(jsonStr, ObjectNode.class);
      if (node.has(field)) {
        node = node.get(field);
      }
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
    return node;
  }
}
