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

package com.kinotic.continuum.internal.core.api.security;

import com.kinotic.continuum.core.api.Scheme;
import com.kinotic.continuum.core.api.security.*;
import com.kinotic.continuum.internal.config.ContinuumIgniteConfigForProfile;
import com.kinotic.continuum.internal.core.api.aignite.IgniteUtils;
import com.kinotic.continuum.internal.utils.ContinuumUtil;
import io.vertx.core.Vertx;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.PathContainer;
import org.springframework.stereotype.Component;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * Created by navid on 1/23/20
 */
@Component
public class DefaultSessionManager implements SessionManager {

    private final SecurityService securityService;
    private final PathPatternParser parser;
    private final Map<String, PathPattern> pathPatternCache = new ConcurrentHashMap<>();
    private final IgniteCache<String, DefaultSessionMetadata> sessionCache;
    private final Scheduler scheduler;

    public DefaultSessionManager(Vertx vertx,
                                 @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
                                 @Autowired(required = false) SecurityService securityService,
                                 @Autowired(required = false) Ignite ignite) {

        this.securityService = securityService;
        this.parser = new PathPatternParser();
        this.parser.setPathOptions(PathContainer.Options.MESSAGE_ROUTE);
        this.parser.setMatchOptionalTrailingSeparator(false);

        // Will be null when running some tests
        if(ignite !=  null){
            sessionCache = ignite.cache(ContinuumIgniteConfigForProfile.SESSION_CACHE_NAME);
        }else{
            sessionCache = null;
        }

        scheduler = Schedulers.fromExecutor(command -> vertx.executeBlocking(v -> command.run(), null));
    }

    @Override
    public Mono<Session> create(String sessionId, Participant participant) {
        Mono<Session> ret = Mono.create(sink -> {

            ParticipantPathPatterns participantPathPatterns = new ParticipantPathPatterns(participant);

            if(sessionCache != null){
                IgniteSession igniteSession = new IgniteSession(participant,
                                                                sessionId,
                                                                parser.getPathOptions(),
                                                                participantPathPatterns.sendPatterns,
                                                                participantPathPatterns.subscriptionPatterns,
                                                                sessionCache);

                DefaultSessionMetadata sessionMetadata = new DefaultSessionMetadata()
                        .sessionId(sessionId)
                        .participantIdentity(participant.getIdentity())
                        .participantType(participant.getMetadata().get(MetadataConstants.TYPE_KEY))
                        .lastUsedDate(new Date());


                IgniteUtils.futureToMono(sessionCache.putAsync(sessionId, sessionMetadata))
                           .subscribe(v -> {},
                                      t -> sink.error(new IllegalStateException("Could not create session", t)),
                                      () -> sink.success(igniteSession));

            }else{
                sink.success(new DefaultSession(participant,
                                                sessionId,
                                                parser.getPathOptions(),
                                                participantPathPatterns.sendPatterns,
                                                participantPathPatterns.subscriptionPatterns));
            }
        });

        return ret.subscribeOn(scheduler);
    }

    @Override
    public Mono<Boolean> removeSession(String sessionId) {
        return IgniteUtils.futureToMono(() -> sessionCache.removeAsync(sessionId)).subscribeOn(scheduler);
    }

    @Override
    public Mono<Session> findSession(String sessionId) {
        Mono<Session> ret;
        if(sessionCache != null && securityService != null){
            ret = IgniteUtils.futureToMono(() -> sessionCache.getAsync(sessionId))
                             .switchIfEmpty(Mono.error(new IllegalArgumentException("No session can be found for the given id")))
                             .zipWhen(sessionMetadata -> securityService.findParticipant(sessionMetadata.participantIdentity()))
                             .map(objects -> {
                                    Participant participant = objects.getT2();
                                    ParticipantPathPatterns participantPathPatterns = new ParticipantPathPatterns(participant);
                                    return new IgniteSession(participant,
                                                             objects.getT1().sessionId(),
                                                             parser.getPathOptions(),
                                                             participantPathPatterns.sendPatterns,
                                                             participantPathPatterns.subscriptionPatterns,
                                                             sessionCache);
                            });
            ret = ret.subscribeOn(scheduler);
        }else{
            ret = Mono.error(new IllegalStateException("Find Session can only be used when Ignite is enabled and a SecurityService is configured"));
        }
        return ret;
    }


    private class ParticipantPathPatterns {
        List<PathPattern> sendPatterns = new LinkedList<>();
        List<PathPattern> subscriptionPatterns = new LinkedList<>();

        public ParticipantPathPatterns(Participant participant) {
            String encodedIdentity = ContinuumUtil.safeEncodeURI(participant.getIdentity());

            // Add send patterns
            for(String path: participant.getPermissions().getAllowedSendPatterns()){
                sendPatterns.add(getPathPattern(path));
            }


            // clients can subscribe to any service that is scoped to their identity
            subscriptionPatterns.add(parser.parse(Scheme.SERVICE.raw() + "://"
                                                          + encodedIdentity
                                                          + "*@*.**"));

            for(String path: participant.getPermissions().getAllowedSubscriptionPatterns()){
                subscriptionPatterns.add(getPathPattern(path));
            }
        }

        private PathPattern getPathPattern(String pattern) {
            return pathPatternCache.computeIfAbsent(pattern, parser::parse);
        }
    }

}
