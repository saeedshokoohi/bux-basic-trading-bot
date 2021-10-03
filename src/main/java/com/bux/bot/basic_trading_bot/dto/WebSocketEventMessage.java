package com.bux.bot.basic_trading_bot.dto;

public class WebSocketEventMessage {
  private String content;

  public WebSocketEventMessage(String content) {
    this.content = content;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }
}
