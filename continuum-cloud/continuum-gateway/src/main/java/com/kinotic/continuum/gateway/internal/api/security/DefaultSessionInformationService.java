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

package com.kinotic.continuum.gateway.internal.api.security;

import com.kinotic.continuum.core.api.event.StreamData;
import com.kinotic.continuum.core.api.security.SessionMetadata;
import com.kinotic.continuum.gateway.api.security.SessionInformationService;
import com.kinotic.continuum.internal.config.ContinuumIgniteConfigForProfile;
import com.kinotic.continuum.internal.core.api.aignite.IgniteContinuousQueryObserver;
import com.kinotic.continuum.internal.core.api.aignite.IgniteUtils;
import com.kinotic.continuum.internal.core.api.security.DefaultSessionMetadata;
import io.vertx.core.Context;
import io.vertx.core.Vertx;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.query.ContinuousQueryWithTransformer;
import org.apache.ignite.cache.query.QueryCursor;
import org.apache.ignite.cache.query.ScanQuery;
import org.apache.ignite.lang.IgniteClosure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import javax.cache.Cache;
import javax.cache.configuration.Factory;
import javax.cache.configuration.FactoryBuilder;
import javax.cache.event.CacheEntryEvent;
import javax.cache.event.CacheEntryEventFilter;
import javax.cache.event.EventType;
import java.util.concurrent.atomic.AtomicLong;

/**
 *
 * Created by Navid Mitchell on 7/1/20
 */
@Component
public class DefaultSessionInformationService implements SessionInformationService {

    private static final Logger log = LoggerFactory.getLogger(DefaultSessionInformationService.class);

    private final Vertx vertx;
    private final Ignite ignite;
    private final IgniteCache<String, DefaultSessionMetadata> sessionCache;
    private final Scheduler scheduler;


    public DefaultSessionInformationService(Vertx vertx,
                                 @Autowired(required = false) Ignite ignite) {

        this.vertx = vertx;
        this.ignite = ignite;

        // Will be null when running some tests
        if(ignite !=  null){
            sessionCache = ignite.cache(ContinuumIgniteConfigForProfile.SESSION_CACHE_NAME);
        }else{
            sessionCache = null;
        }

        scheduler = Schedulers.fromExecutor(command -> vertx.executeBlocking(v -> command.run(), null));
    }

    @Override
    public Flux<Long> countActiveSessionsContinuous() {
        if(ignite == null){
            throw new IllegalStateException("This method is not available when ignite is disabled");
        }
        Flux<Long> ret;

        ret = Flux.create(sink -> {
            AtomicLong activeSessionsCount = new AtomicLong();

            Context vertxContext = vertx.getOrCreateContext();

            ContinuousQueryWithTransformer<String, DefaultSessionMetadata, Long> qry = new ContinuousQueryWithTransformer<>();
            qry.setIncludeExpired(true);

            Factory<IgniteClosure<CacheEntryEvent<? extends String, ? extends DefaultSessionMetadata>, Long>> transformerFactory = FactoryBuilder
                    .factoryOf(
                            (IgniteClosure<CacheEntryEvent<? extends String, ? extends DefaultSessionMetadata>, Long>) event  -> {
                                long change = 0;
                                if(event.getEventType() == EventType.CREATED){
                                    change = 1L;
                                }else if(event.getEventType() == EventType.REMOVED || event.getEventType() == EventType.EXPIRED){
                                    change = -1L;
                                }
                                return change;
                            });

            qry.setRemoteTransformerFactory(transformerFactory);

            qry.setRemoteFilterFactory((Factory<CacheEntryEventFilter<String, DefaultSessionMetadata>>)
                                               () -> event -> event.getEventType() != EventType.UPDATED);

            qry.setLocalListener(events -> vertxContext.runOnContext(v ->{
                for(Long change  : events){
                    if(!sink.isCancelled()) {
                        sink.next(activeSessionsCount.addAndGet(change));
                    }
                }
            }));

            // Executing the query.
            QueryCursor<Cache.Entry<String, DefaultSessionMetadata>> cursor = null;
            try {
                // Not sure but it seems we could lose some updates here
                long currentSize = sessionCache.sizeLong();
                activeSessionsCount.set(currentSize);
                sink.next(activeSessionsCount.get());

                cursor = sessionCache.query(qry);

                QueryCursor<Cache.Entry<String, DefaultSessionMetadata>> finalCursor = cursor;
                sink.onDispose(() -> safeCloseCursor(finalCursor));

            }catch (Exception e){
                safeCloseCursor(cursor);
                sink.error(e);
            }

        });

        return ret.subscribeOn(scheduler);
    }

    @Override
    public Flux<StreamData<String, SessionMetadata>> listActiveSessionsContinuous() {
        if(ignite == null){
            throw new IllegalStateException("This method is not available when ignite is disabled");
        }
        Flux<StreamData<String, SessionMetadata>> ret;

        ret = IgniteUtils.observerToFlux(() -> new IgniteContinuousQueryObserver<>(vertx, sessionCache, new ScanQuery<>()));
        return ret.subscribeOn(scheduler);
    }

    private void safeCloseCursor(QueryCursor<?> cursor){
        try {
            if(cursor != null) {
                cursor.close();
            }
        } catch (Exception ex) {
            log.warn("Exception closing continuous query",ex);
        }
    }
}
