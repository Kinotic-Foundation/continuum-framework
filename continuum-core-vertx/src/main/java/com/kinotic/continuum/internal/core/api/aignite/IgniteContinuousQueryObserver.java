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
import com.kinotic.continuum.internal.util.IgniteUtils;
import io.vertx.core.*;
import org.apache.commons.lang3.Validate;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.query.ContinuousQuery;
import org.apache.ignite.cache.query.Query;
import org.apache.ignite.cache.query.SqlQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.cache.Cache;
import javax.cache.event.CacheEntryEvent;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Using this as a bridge between tested legacy Ignite logic and the new code base
 * This will be converted to a {@link reactor.core.publisher.Flux} style architecture vs implementing {@link Observer}
 * Created by Navid Mitchell on 3/29/17.
 */
public class IgniteContinuousQueryObserver<K, V> implements Observer<StreamData<K,V>>, Closeable{

    private static final Logger log = LoggerFactory.getLogger(IgniteContinuousQueryObserver.class);

    private AtomicBoolean closed = new AtomicBoolean(false);
    private Handler<StreamData<K, V>> observerDataHandler =null;
    private Handler<Throwable> observerExceptionHandler = null;
    private final Context observerContext;
    private final Vertx vertx;
    private IgniteCache<? extends K,? extends V> igniteCache;
    private Query<Cache.Entry<K, V>> query;
    private IterableEventLooper<Cache.Entry<K,V>> looper;

    public IgniteContinuousQueryObserver(Vertx vertx,
                                         IgniteCache<? extends K, ? extends V> igniteCache) {
        Validate.notNull(vertx);
        Validate.notNull(igniteCache);

        this.vertx = vertx;
        this.observerContext = vertx.getOrCreateContext();

        if (observerContext.isMultiThreadedWorkerContext()) {
            throw new IllegalStateException("Cannot use IgniteContinuousQueryObserver in a multi-threaded worker verticle");
        }
        observerContext.addCloseHook(this);
        this.igniteCache = igniteCache;
    }

    public IgniteContinuousQueryObserver(Vertx vertx,
                                         IgniteCache<? extends K, ? extends V> igniteCache,
                                         Query<Cache.Entry<K, V>> query) {
        this(vertx, igniteCache);
        Validate.notNull(query);
        this.query = query;
    }


    @Override
    public Observer<StreamData<K,V>> handler(Handler<StreamData<K,V>> handler) {
        this.observerDataHandler = handler;
        return this;
    }

    @Override
    public Observer<StreamData<K,V>> exceptionHandler(Handler<Throwable> handler) {
        this.observerExceptionHandler = handler;
        return this;
    }

    @Override
    public Observer<StreamData<K,V>> completionHandler(Handler<Void> handler) {
        // throw away since this never ends
        return this;
    }


    @Override
    public void start() {
        if(observerDataHandler == null){
            throw new IllegalStateException("You must set the handler before calling start");
        }
        if(closed.get()){
            throw new IllegalStateException("You cannot call start after calling close or after completed");
        }


        ContinuousQuery<K,V> continuousQuery = new ContinuousQuery<>();
        if(query != null) {
            continuousQuery.setInitialQuery(query);
        }

        // Set filter for continuous results
        if(query instanceof SqlQuery){
            SqlQuery sql = ((SqlQuery)query);
            continuousQuery.setRemoteFilterFactory(new ScriptCacheEntryFilterFactory<>(sql.getSql(),sql.getArgs()));
        }

        // Set local listener to receive continuous results
        continuousQuery.setLocalListener(cacheEntryEvents -> {

            final Iterable<CacheEntryEvent<? extends K, ? extends V>> events = cacheEntryEvents;
            observerContext.runOnContext(ce -> {
                IterableEventLooper<CacheEntryEvent<? extends K, ? extends V>>
                    updatesLooper = new IterableEventLooper<>(vertx, events);

                updatesLooper.handler(new ConverterHandler<>(IgniteUtils::cacheEntryEventToStreamData,
                                                             observerDataHandler));

                updatesLooper.exceptionHandler(this::invokeObserverExceptionHandler);

                updatesLooper.start();
            });

        });


        observerContext.executeBlocking((Handler<Promise<Void>>) ev -> {
            try{

                // Loop through initial results from the query sending them to the client
                looper = new IterableEventLooper<>(vertx, igniteCache.query(continuousQuery), false);

                looper.handler(new ConverterHandler<>(IgniteUtils::cacheEntryToStreamData, observerDataHandler));

                looper.exceptionHandler(this::invokeObserverExceptionHandler);
                looper.start();

            }catch (Exception exc){
                invokeObserverExceptionHandler(exc);
            }
        },null);
    }


    private void invokeObserverExceptionHandler(Throwable e) {
        try {
            close();
        }catch (Exception exc){
            log.warn("Exception closing IgniteContinuousQueryObserver", exc);
        }
        if(observerExceptionHandler != null){
            observerContext.runOnContext(event1 -> {
                try {
                    observerExceptionHandler.handle(e);
                } catch (Exception e1) {
                    log.warn("Query Observer's error handler threw an error "+e.getMessage());
                }
            });
        }
    }

    @Override
    public void close() {
        this.close(null);
    }

    @Override
    public void close(final Handler<AsyncResult<Void>> completionHandler) {
        if(log.isTraceEnabled()){
            log.trace("Closing Continuous query");
        }
        closed.set(true);
        if(looper != null){
            looper.close();
        }

        if (observerContext != null) {
            observerContext.removeCloseHook(this);
        }

        if (completionHandler != null) {
            Context context = vertx.getOrCreateContext();
            context.runOnContext(v -> completionHandler.handle(Future.succeededFuture()));
        }
    }
}
