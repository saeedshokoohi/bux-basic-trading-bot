package com.bux.bot.basic_trading_bot.client.websocket.bux_impl;

public class WebSocketMockData {
  public static final String SUBSCRIBE_TO_MESSAGE_1 =
      "\"subscribeTo\": [\n" + " \"trading.product.2223\"\n" + " ],\n";
    public static final String SUBSCRIBE_TO_MESSAGE_2 =
            "\"subscribeTo\": [\n" + " \"trading.product.87875\"\n" + " ],\n";
  public static final String INPUT_MESSAGE1 =
      "{\n"
          + " \"t\": \"trading.quote\",\n"
          + " \"body\": {\n"
          + " \"securityId\": \"11qweqw\",\n"
          + " \"currentPrice\": \"10692.3\"\n"
          + " }\n"
          + "}\n";
  public static final String INPUT_MESSAGE2 =
          "{\n"
                  + " \"t\": \"trading.quote\",\n"
                  + " \"body\": {\n"
                  + " \"securityId\": \"we1221\",\n"
                  + " \"currentPrice\": \"10692.3\"\n"
                  + " }\n"
                  + "}\n";
    public static String connectionResponse="{\n" +
            " \"t\": \"connect.connected\",\n" +
            " \"body\": {\n" +

            " }\n" +
            " }\n" +
            "}\n";
  public static final String INPUT_MESSAGE_productp32112 =
          "{\n"
                  + " \"t\": \"trading.quote\",\n"
                  + " \"body\": {\n"
                  + " \"securityId\": \"p32112\",\n"
                  + " \"currentPrice\": \"105\"\n"
                  + " }\n"
                  + "}\n";
  public static final String INPUT_MESSAGE_productp32112_goodPrice_tobuy =
          "{\n"
                  + " \"t\": \"trading.quote\",\n"
                  + " \"body\": {\n"
                  + " \"securityId\": \"p32112\",\n"
                  + " \"currentPrice\": \"100\"\n"
                  + " }\n"
                  + "}\n";
  public static final String INPUT_MESSAGE_productp32112_goodPrice_tosellWithProfit =
          "{\n"
                  + " \"t\": \"trading.quote\",\n"
                  + " \"body\": {\n"
                  + " \"securityId\": \"p32112\",\n"
                  + " \"currentPrice\": \"120\"\n"
                  + " }\n"
                  + "}\n";
}
