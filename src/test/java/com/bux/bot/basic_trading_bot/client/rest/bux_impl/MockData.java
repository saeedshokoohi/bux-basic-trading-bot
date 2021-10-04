package com.bux.bot.basic_trading_bot.client.rest.bux_impl;

public class MockData {
  public static final String OPEN_POSITION_REQUEST =
      "{\n"
          + " \"productId\" : \"26618\",\n"
          + " \"investingAmount\" : {\n"
          + " \"currency\": \"BUX\",\n"
          + " \"decimals\": 2,\n"
          + " \"amount\": \"10.00\"\n"
          + " },\n"
          + " \"leverage\" : 2,\n"
          + " \"direction\" : \"BUY\",\n"
          + " \"source\": {\n"
          + " \"sourceType\": \"SHARED_TRADE\",\n"
          + " \"sourceId\": \"37e5158e-6f74-4446-85f7-47d037af4145\"\n"
          + " },\n"
          + " \"riskWarningConfirmation\": \"I agree that I know what I'm doing\"\n"
          + "}";
  public static final String SUCCESSFUL_OPEN_POSITION_RESPONSE =
      "{\n"
          + " \"id\": \"9bc887eb-23d2-4293-a139-1da9c4eb5f1c\",\n"
          + " \"positionId\": \"655ddda5-fd6d-48a9-800d-b7b93eb041af\",\n"
          + " \"product\": {\n"
          + " \"securityId\": \"26618\",\n"
          + " \"symbol\": \"EUR/USD\",\n"
          + " \"displayName\": \"EUR/USD\"\n"
          + " },\n"
          + " \"investingAmount\": {\n"
          + " \"currency\": \"BUX\",\n"
          + " \"decimals\": 2,\n"
          + " \"amount\": \"10.00\"\n"
          + " },\n"
          + " \"price\": {\n"
          + " \"currency\": \"USD\",\n"
          + " \"decimals\": 5,\n"
          + " \"amount\": \"1.07250\"\n"
          + " },\n"
          + " \"leverage\": 2,\n"
          + " \"direction\": \"BUY\",\n"
          + " \"type\": \"OPEN\",\n"
          + " \"dateCreated\": 1492601296549\n"
          + "}\n";
  public static final String FAILED_OPEN_POSITION_RESPONSE =
      "{\n"
          + " \"message\": \"may be null\",\n"
          + " \"developerMessage\": \"technical description of the error\",\n"
          + " \"errorCode\": \"AUTH_001\"\n"
          + "}\n";
    public static final String SUCCESSFUL_CLOSE_POSITION_RESPONSE = "{\n" +
            " \"id\": \"f2a673d9-a457-4ab6-8c84-8ec81ed0c7ab\",\n" +
            " \"positionId\": \"4c58a0b2-ea78-46a0-ac21-5a8c22d527dc\",\n" +
            " \"profitAndLoss\": {\n" +
            " \"currency\": \"BUX\",\n" +
            " \"decimals\": 2,\n" +
            " \"amount\": \"-0.71\"\n" +
            " },\n" +
            " \"product\": {\n" +
            " \"securityId\": \"26623\",\n" +
            " \"symbol\": \"IM.AS\",\n" +
            " \"displayName\": \"IMTECH\"\n" +
            " },\n" +
            " \"investingAmount\": {\n" +
            " \"currency\": \"BUX\",\n" +
            " \"decimals\": 2,\n" +
            " \"amount\": \"200.00\"\n" +
            " },\n" +
            " \"price\": {\n" +
            " \"currency\": \"EUR\",\n" +
            " \"decimals\": 3,\n" +
            " \"amount\": \"0.565\"\n" +
            " },\n" +
            " \"leverage\": 1,\n" +
            " \"direction\": \"SELL\",\n" +
            " \"type\": \"CLOSE\",\n" +
            " \"dateCreated\": 1405515554326\n" +
            "}\n";
  public static final String FAILED_CLOSE_POSITION_RESPONSE =
      "{\n"
          + " \"message\": \"may be null\",\n"
          + " \"developerMessage\": \"technical description of the error\",\n"
          + " \"errorCode\": \"AUTH_001\"\n"
          + "}";
}
