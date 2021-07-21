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

package com.kinotic.continuum.gateway.internal.endpoints;

import com.kinotic.continuum.core.api.CRI;
import com.kinotic.continuum.core.api.Scheme;
import com.kinotic.continuum.core.api.event.Event;
import com.kinotic.continuum.core.api.event.EventConstants;
import com.kinotic.continuum.core.api.security.Participant;
import com.kinotic.continuum.core.api.security.Session;
import com.kinotic.continuum.internal.util.SecurityUtil;
import io.vertx.core.Promise;
import io.vertx.ext.stomp.frame.Frame;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Generic class to perform {@link com.kinotic.continuum.core.api.event.Event} handling coming from various endpoints
 *
 *
 * Created by Navid Mitchell on 11/3/20
 */
public class EndpointConnectionHandler {

    private static final Logger log = LoggerFactory.getLogger(EndpointConnectionHandler.class);

    private final Services services;

    private final Map<String, BaseSubscriber<Event<byte[]>>> subscriptions = new HashMap<>();
    private Session session;
    private long sessionTimer = -1;

    public EndpointConnectionHandler(Services services) {
        this.services = services;
    }

    public Promise<Map<String, String>> authenticate(Map<String, String> connectHeaders) {

        Promise<Map<String, String>> ret = Promise.promise();

        // Check if session is being used to authenticate
        if(connectHeaders.containsKey(EventConstants.SESSION_HEADER)){

            String sessionId = connectHeaders.get(EventConstants.SESSION_HEADER);
            services.sessionManager
                    .findSession(sessionId)
                    .subscribe(s -> {
                        sessionActive(s);
                        ret.complete(Collections.singletonMap(EventConstants.SESSION_HEADER, session.sessionId()));
                    }, throwable -> {
                        log.error("Session could not be found "+sessionId, throwable);
                        ret.fail("Session is invalid");
                    });

        }else{
            String identity = connectHeaders.get(Frame.LOGIN);
            String secret = connectHeaders.get(Frame.PASSCODE);
            if (StringUtils.isNotBlank(identity) && StringUtils.isNotBlank(secret)) {
                services.securityService
                        .authenticate(identity, secret)
                        .flatMap((Function<Participant, Mono<Session>>) participant -> {

                            try {
                                SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
                                KeySpec spec = new PBEKeySpec(secret.toCharArray(),
                                                              SecurityUtil.generateSecretKey(128),
                                                              65536,
                                                              256);
                                SecretKey sessionSecretKey = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");

                                String sessionId = Base64.getUrlEncoder()
                                                         .withoutPadding()
                                                         .encodeToString(sessionSecretKey.getEncoded());

                                return services.sessionManager.create(sessionId, participant);

                            } catch (Exception e) {
                                log.error("Session could not be created for identity: "+identity, e);
                                return Mono.error(new IllegalStateException("Session could not be created",e));
                            }
                        })
                        .subscribe(s -> {

                            sessionActive(s);

                            ret.complete(Collections.singletonMap(EventConstants.SESSION_HEADER, session.sessionId()));
                        }, ret::fail);
            } else {
                ret.fail("The connection frame does not contain valid credentials");
            }
        }
        return ret;
    }

    private void sessionActive(Session session){
        this.session = session;
        // update session at least every half the time of the timeout
        long sessionUpdateInterval = services.continuumProperties.getSessionTimeout() / 2;
        sessionTimer = services.vertx.setPeriodic(sessionUpdateInterval, event -> this.session.touch());
    }

    public Mono<Void> send(Event<byte[]> event) {
        Mono<Void> ret;
        if (session.sendAllowed(event.cri())) {

            event.metadata().put(EventConstants.SENDER_HEADER, session.participant().getIdentity());

            if (event.cri().scheme() == Scheme.SERVICE) {

                ret = services.eventBusService.sendWithAck(event);

            } else if (event.cri().scheme() == Scheme.STREAM) {

                Mono<Void> hftMono = services.hftQueueManager.write(event)
                                                             .onErrorMap(t -> {
                                                                 log.error("Error occurred writing to HFT Queue", t);
                                                                 return new IllegalStateException("Could not store");
                                                             });

                Mono<Void> streamMono = services.eventStreamService.send(event);

                ret = Flux.concat(hftMono, streamMono)
                          .then();

            } else {
                ret = Mono.error(new IllegalArgumentException("CRI scheme not supported"));
            }
        } else {
            ret = Mono.error(new IllegalArgumentException("Not Authorized to send to " + event.cri()));
        }
        return ret;
    }

    public void subscribe(CRI cri, String subscriptionIdentifier, BaseSubscriber<Event<byte[]>> subscriber){
        Validate.notNull(cri, "CRI must not be null");
        Validate.notEmpty(subscriptionIdentifier,"subscriptionIdentifier must not be empty");
        Validate.notNull(subscriber, "Subscriber must not be null");

        if(!session.subscribeAllowed(cri)){
            throw new IllegalArgumentException("Not Authorized to subscribe to "+cri);
        }

        if (cri.scheme() == Scheme.SERVICE) {

            services.eventBusService.listen(cri.baseResource()).subscribe(subscriber);

            subscriptions.put(subscriptionIdentifier, subscriber);

            if(log.isDebugEnabled()){
                log.debug("New subscription cri: "+cri.raw()+" id: "+subscriptionIdentifier+" for login: "+ session.participant());
            }

        } else if (cri.scheme() == Scheme.STREAM) {

            services.eventStreamService.listen(cri).subscribe(subscriber);

            subscriptions.put(subscriptionIdentifier, subscriber);

            if(log.isDebugEnabled()){
                log.debug("New subscription cri: "+cri.raw()+" id: "+subscriptionIdentifier+" for login: "+ session.participant());
            }

        } else {
            throw new IllegalArgumentException("CRI scheme not supported");
        }
    }


    public void unsubscribe(String subscriptionIdentifier){
        Validate.notEmpty(subscriptionIdentifier,"subscriptionIdentifier must not be empty");

        if(subscriptions.containsKey(subscriptionIdentifier)){
            subscriptions.remove(subscriptionIdentifier).cancel();
        }else{
            log.debug("No subscription exists for subscriptionIdentifier: "+subscriptionIdentifier);
        }

    }

    public void removeSession(){
        if(session != null){
            services.sessionManager
                    .removeSession(session.sessionId())
                    .subscribe(value -> {
                                   if(!value){
                                       log.error("Could not remove sessionId: "+session.sessionId());
                                   }
                               },
                               throwable -> log.error("Could not remove sessionId: "+session.sessionId(), throwable));
        }else{
            log.error("No session for connection was set");
        }
    }

    public void shutdown(){
        if(sessionTimer != -1){
            services.vertx.cancelTimer(sessionTimer);
        }

        subscriptions.forEach((s, messageConsumer) -> messageConsumer.cancel());
        subscriptions.clear();
    }


}
