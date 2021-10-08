package com.bux.bot.basic_trading_bot.util;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

public class FluxText {

    @Test
    void testingFluxMerge() throws InterruptedException {
        List<String> strr=List.of("One","Two","Three","Four","Five","Six","seventh","to");
        Flux<Integer> numberStreams1=Flux.range(0,10).delayElements(Duration.ofMillis(20));
        Flux<String> numberStreams2=Flux.fromIterable(strr).delayElements(Duration.ofMillis(10));;


    numberStreams1.log().doOnNext(
        number -> {
          numberStreams2.log().filter(str->str.length()==number).flatMap(
              str -> {
                return print(str,number);

              }).doOnNext(mix->System.out.println(mix)).subscribe();
        }).subscribe();

   Thread.sleep(50000);
    }

    Mono<String> print(String str,Integer number)
    {
       return Mono.create(emitter->{
           try {
               Thread.sleep(1000);
           } catch (InterruptedException e) {
               e.printStackTrace();
           }
           emitter.success(str+" "+number);

       });

    }
}
