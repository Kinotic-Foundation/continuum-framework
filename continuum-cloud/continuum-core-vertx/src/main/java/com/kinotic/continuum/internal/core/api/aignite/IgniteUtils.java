/*
 *
 * Copyright 2008-2021 Kinotic and the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kinotic.continuum.internal.core.api.aignite;

import com.kinotic.continuum.core.api.event.StreamData;
import com.kinotic.continuum.core.api.event.StreamOperation;
import org.apache.ignite.lang.IgniteFuture;
import org.apache.ignite.lang.IgniteInClosure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoSink;

import javax.cache.Cache;
import javax.cache.event.CacheEntryEvent;
import javax.cache.event.EventType;
import java.util.function.Supplier;

/**
 *
 * Created by Navid Mitchell on 5/29/20
 */
public class IgniteUtils {
    private static final Logger log = LoggerFactory.getLogger(IgniteUtils.class);

    public static <T> Mono<T> futureToMono(IgniteFuture<T> future){
        return Mono.create(sink ->  {
            try{
                futureToMonoSink(sink, future);
            }catch (Exception e){
                sink.error(e);
            }
        });
    }


    public static <T> Mono<T> futureToMono(Supplier<IgniteFuture<T>> futureSupplier){
        return Mono.create(sink ->  {
            try{
                futureToMonoSink(sink, futureSupplier.get());
            }catch (Exception e){
                sink.error(e);
            }
        });
    }

    public static <T> void futureToMonoSink(MonoSink<T> sink, IgniteFuture<T> igniteFuture){
        igniteFuture.listen((IgniteInClosure<IgniteFuture<T>>) future -> {
            try{
                sink.success(future.get());
            }catch (Exception ex){
                sink.error(ex);
            }
        });
    }

    public static <T> Flux<T> observerToFlux(Supplier<Observer<T>> observerSupplier){
        return Flux.create(sink -> {
            Observer<T> observer = observerSupplier.get();
            observer.completionHandler(v -> sink.complete());
            observer.exceptionHandler(sink::error);
            observer.handler(sink::next);
            sink.onCancel(() -> {
                try {
                    observer.close();
                } catch (Exception e) {
                    log.error("Exception trying to close Observer",e);
                }
            });
            observer.start();
        });
    }



    public static <I,T> StreamData<I,T> cacheEntryEventToStreamData(CacheEntryEvent<? extends I,? extends T> cacheEntryEvent){
        StreamData<I,T> ret;

        StreamOperation operation = toStreamOperation(cacheEntryEvent.getEventType());
        // use the old value on remove so we can use in remote listener if desired
        if(operation == StreamOperation.REMOVE){
            ret = new StreamData<>(operation, cacheEntryEvent.getKey(), cacheEntryEvent.getOldValue());
        }else{
            ret = new StreamData<>(operation, cacheEntryEvent.getKey(), cacheEntryEvent.getValue());
        }
        return ret;
    }

    public static <I,T> StreamData<I,T> cacheEntryToStreamData(Cache.Entry<I, T> cacheEntry){
        return new StreamData<>(StreamOperation.EXISTING,
                                cacheEntry.getKey(),
                                cacheEntry.getValue());
    }

    public static StreamOperation toStreamOperation(EventType eventType){
        StreamOperation ret;
        switch (eventType){
            case CREATED:
                ret = StreamOperation.EXISTING;
                break;
            case UPDATED:
                ret = StreamOperation.UPDATE;
                break;
            case REMOVED:
            case EXPIRED:
                ret = StreamOperation.REMOVE;
                break;
            default:
                throw new IllegalArgumentException("Unknown EventType "+eventType.name());
        }
        return ret;
    }
}
