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

import io.vertx.core.eventbus.impl.clustered.ClusterNodeInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.cache.event.CacheEntryEvent;
import javax.cache.event.CacheEntryEventFilter;
import javax.cache.event.CacheEntryListenerException;
import java.io.Serializable;
import java.util.Set;

/**
 * {@link CacheEntryEventFilter} for {@link ClusterNodeInfo}
 * Created by 🤓 on 5/8/21.
 */
public class SubscriptionInfoCacheEntryEventFilter implements CacheEntryEventFilter<String, Set<ClusterNodeInfo>>, Serializable {

    private static final Logger log = LoggerFactory.getLogger(SubscriptionInfoCacheEntryEventFilter.class);

    private final String cri;

    public SubscriptionInfoCacheEntryEventFilter(String cri) {
        this.cri = cri;
    }

    @Override
    public boolean evaluate(CacheEntryEvent<? extends String, ? extends Set<ClusterNodeInfo>> event) throws CacheEntryListenerException {
        boolean match = event.getKey().equals(cri);
        if(log.isTraceEnabled()) {
            log.trace("Subscription Status: " + event.getEventType().name()
                      + " Received for " + event.getKey()
                      + " waiting for " + cri + (match ? " they match." : " they don't match."));
        }
        return match;
    }
}
