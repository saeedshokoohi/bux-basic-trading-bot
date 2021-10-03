package com.bux.bot.basic_trading_bot.event;

import com.sun.istack.NotNull;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class WebSocketEventBus {

  private Set<WebSocketEventObserver> inputObservers;
  private Set<WebSocketEventObserver> outputObservers;
  private Set<WebSocketEventObserver> connectionObservers;

  public WebSocketEventBus() {
    inputObservers=new HashSet<>();
    outputObservers=new HashSet<>();
    connectionObservers=new HashSet<>();

  }

  public Set<WebSocketEventObserver> getInputObservers() {
    return inputObservers;
  }

  public Set<WebSocketEventObserver> getOutputObservers() {
    return outputObservers;
  }

  public Set<WebSocketEventObserver> getConnectionObservers() {
    return connectionObservers;
  }

  /***
   * subscribing to event handler
   * @param observer
   */
  public boolean subscribeOnInput(@NotNull WebSocketEventObserver observer) {
    return inputObservers.add(observer);
  }
  public boolean subscribeOnConnection(@NotNull WebSocketEventObserver observer) {
    return connectionObservers.add(observer);
  }
  public boolean subscribeOnOutput(@NotNull WebSocketEventObserver observer) {
    return outputObservers.add(observer);
  }

  /***
   * emitting to event handler
   * @param event
   */
  public void emitToInput(WebSocketEvent event) {
    inputObservers.forEach(observer -> observer.next(event));
  }
  public void emitToOutput(WebSocketEvent event) {
    outputObservers.forEach(observer -> observer.next(event));
  }
  public void emitToConnection(WebSocketEvent event) {
    connectionObservers.forEach(observer -> observer.next(event));
  }

  /***
   * unsubscribing from event handler
   * @param observer
   * @return true if there were giving observer in observer list
   * @return false if the given observer is not in observers list
   */
  public boolean unsubscribe(@NotNull WebSocketEventObserver observer) {
    boolean unSubscribed = false;
    if (this.inputObservers != null) {
      unSubscribed = this.inputObservers.remove(observer);
    }else if(!unSubscribed && this.outputObservers!=null)
    {
      unSubscribed = this.outputObservers.remove(observer);
    }else if(!unSubscribed && this.connectionObservers!=null)
    {
      unSubscribed = this.connectionObservers.remove(observer);
    }
    return unSubscribed;
  }
}
