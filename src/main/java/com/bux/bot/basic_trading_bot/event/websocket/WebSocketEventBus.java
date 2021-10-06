package com.bux.bot.basic_trading_bot.event.websocket;

import com.sun.istack.NotNull;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class WebSocketEventBus {

  private Set<WebSocketEventObserver> inputChannelObservers;
  private Set<WebSocketEventObserver> outputChannelObservers;
  private Set<WebSocketEventObserver> connectionChannelObservers;

  public WebSocketEventBus() {
    inputChannelObservers =new HashSet<>();
    outputChannelObservers =new HashSet<>();
    connectionChannelObservers =new HashSet<>();

  }

  public Set<WebSocketEventObserver> getInputChannelObservers() {
    return inputChannelObservers;
  }

  public Set<WebSocketEventObserver> getOutputChannelObservers() {
    return outputChannelObservers;
  }

  public Set<WebSocketEventObserver> getConnectionChannelObservers() {
    return connectionChannelObservers;
  }

  /***
   * subscribing to event handler
   * @param observer
   */
  public boolean subscribeOnInput(@NotNull WebSocketEventObserver observer) {
    return inputChannelObservers.add(observer);
  }
  public boolean subscribeOnConnection(@NotNull WebSocketEventObserver observer) {
    return connectionChannelObservers.add(observer);
  }
  public boolean subscribeOnOutput(@NotNull WebSocketEventObserver observer) {
    return outputChannelObservers.add(observer);
  }

  /***
   * emitting to event handler
   * @param event
   */
  public void emitToInput(WebSocketEvent event) {
    inputChannelObservers.forEach(observer -> observer.next(event));
  }
  public void emitToOutput(WebSocketEvent event) {
    outputChannelObservers.forEach(observer -> observer.next(event));
  }
  public void emitToConnection(WebSocketEvent event) {
    connectionChannelObservers.forEach(observer -> observer.next(event));
  }

  /***
   * unsubscribing from event handler
   * @param observer
   * @return true if there were giving observer in observer list
   * @return false if the given observer is not in observers list
   */
  public boolean unsubscribe(@NotNull WebSocketEventObserver observer) {
    boolean unSubscribed = false;
    if (this.inputChannelObservers != null) {
      unSubscribed = this.inputChannelObservers.remove(observer);
    }else if(!unSubscribed && this.outputChannelObservers !=null)
    {
      unSubscribed = this.outputChannelObservers.remove(observer);
    }else if(!unSubscribed && this.connectionChannelObservers !=null)
    {
      unSubscribed = this.connectionChannelObservers.remove(observer);
    }
    return unSubscribed;
  }
}
