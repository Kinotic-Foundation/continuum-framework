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

package org.kinotic.continuum.gateway.internal.api.security;

import org.kinotic.continuum.core.api.event.StreamData;
import org.kinotic.continuum.core.api.security.SessionMetadata;
import org.kinotic.continuum.gateway.api.security.SessionInformationService;
import org.kinotic.continuum.internal.config.IgniteCacheConstants;
import org.kinotic.continuum.internal.core.api.aignite.IgniteContinuousQueryObserver;
import org.kinotic.continuum.internal.utils.IgniteUtil;
import org.kinotic.continuum.internal.core.api.security.DefaultSessionMetadata;
import io.vertx.core.Vertx;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.query.QueryCursor;
import org.apache.ignite.cache.query.ScanQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

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
            sessionCache = ignite.cache(IgniteCacheConstants.SESSION_CACHE_NAME);
        }else{
            sessionCache = null;
        }

        scheduler = Schedulers.fromExecutor(command -> vertx.executeBlocking(v -> command.run(), null));
    }

    @Override
    public Flux<Long> countActiveSessionsContinuous() {
        return IgniteUtil.countCacheEntriesContinuous(ignite, vertx, sessionCache);
    }

    @Override
    public Flux<StreamData<String, SessionMetadata>> listActiveSessionsContinuous() {
        if(ignite == null){
            throw new IllegalStateException("This method is not available when ignite is disabled");
        }
        Flux<StreamData<String, SessionMetadata>> ret;

        ret = IgniteUtil.observerToFlux(() -> new IgniteContinuousQueryObserver<>(vertx, sessionCache, new ScanQuery<>()));
        return ret.subscribeOn(scheduler);
    }

}
