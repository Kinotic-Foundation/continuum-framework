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

package org.kinotic.continuum.gateway.internal.endpoints;

import org.kinotic.continuum.api.exceptions.RpcMissingServiceException;
import org.kinotic.continuum.core.api.event.CRI;
import org.kinotic.continuum.core.api.event.Event;
import org.kinotic.continuum.core.api.event.EventConstants;
import org.kinotic.continuum.core.api.security.Participant;
import org.kinotic.continuum.core.api.security.Session;
import org.kinotic.continuum.internal.util.SecurityUtil;
import org.kinotic.continuum.internal.utils.ContinuumUtil;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.ReplyException;
import io.vertx.core.eventbus.ReplyFailure;
import io.vertx.ext.stomp.lite.frame.Frame;
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
 * Generic class to perform {@link Event} handling coming from various endpoints
 * <p>
 * <p>
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
        if (connectHeaders.containsKey(EventConstants.SESSION_HEADER)) {

            String sessionId = connectHeaders.get(EventConstants.SESSION_HEADER);
            services.sessionManager
                    .findSession(sessionId)
                    .subscribe(s -> {
                        sessionActive(s);
                        ret.complete(Collections.singletonMap(EventConstants.SESSION_HEADER, session.sessionId()));
                    }, throwable -> {
                        log.error("Session could not be found " + sessionId, throwable);
                        ret.fail("Session is invalid");
                    });

        } else {
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
                                log.error("Session could not be created for identity: " + identity, e);
                                return Mono.error(new IllegalStateException("Session could not be created", e));
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

    private void sessionActive(Session session) {
        this.session = session;
        // update session at least every half the time of the timeout
        long sessionUpdateInterval = services.continuumProperties.getSessionTimeout() / 2;
        sessionTimer = services.vertx.setPeriodic(sessionUpdateInterval, event -> this.session.touch());
    }

    public Mono<Void> send(Event<byte[]> event) {
        Mono<Void> ret;
        if (session.sendAllowed(event.cri())) {

            event.metadata().put(EventConstants.SENDER_HEADER, session.participant().getId());

            if (event.cri().scheme().equals(EventConstants.SERVICE_DESTINATION_SCHEME)) {

                try {
                    // make sure reply-to if present is scoped to sender
                    validateReplyTo(event);

                    ret = services.eventBusService
                            .sendWithAck(event)
                            .onErrorMap(throwable -> { // map errors that occurred because no Service invoker was listening
                                boolean predicateRet = false;
                                if (throwable instanceof ReplyException) {
                                    ReplyException replyException = (ReplyException) throwable;
                                    if (replyException.failureType() == ReplyFailure.NO_HANDLERS) {
                                        predicateRet = true;
                                    }
                                }
                                return predicateRet;
                            }, RpcMissingServiceException::new);

                } catch (Exception e) {
                    ret = Mono.error(e);
                }

            } else if (event.cri().scheme().equals(EventConstants.STREAM_DESTINATION_SCHEME)) {

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

    private void validateReplyTo(Event<byte[]> event) {
        String replyTo = event.metadata().get(EventConstants.REPLY_TO_HEADER);
        if (replyTo != null) {
            // reply-to must not use any * characters and must be "scoped" to the participant identity
            if (replyTo.contains("*")) {
                throw new IllegalArgumentException("reply-to header invalid * are not allowed");
            }

            CRI replyCRI;
            try {
                replyCRI = CRI.create(replyTo);
            } catch (Exception e) {
                throw new IllegalArgumentException("reply-to header invalid " + e.getMessage());
            }

            String scope = replyCRI.scope();
            if (scope != null) {
                // valid scopes are PARTICIPANT_IDENTITY:UUID or PARTICIPANT_IDENTITY
                int idx = scope.indexOf(":");
                if (idx != -1) {
                    scope = scope.substring(0, idx);
                }
                String encodedSender = ContinuumUtil.safeEncodeURI(session.participant().getId());
                if (!scope.equals(encodedSender)) {
                    throw new IllegalArgumentException("reply-to header invalid, scope: " + scope + " is not valid for authenticated participant");
                }
            }
        }
    }

    public void subscribe(CRI cri, String subscriptionIdentifier, BaseSubscriber<Event<byte[]>> subscriber) {
        Validate.notNull(cri, "CRI must not be null");
        Validate.notEmpty(subscriptionIdentifier, "subscriptionIdentifier must not be empty");
        Validate.notNull(subscriber, "Subscriber must not be null");

        if (!session.subscribeAllowed(cri)) {
            throw new IllegalArgumentException("Not Authorized to subscribe to " + cri);
        }

        if (cri.scheme().equals(EventConstants.SERVICE_DESTINATION_SCHEME)) {

            services.eventBusService.listen(cri.baseResource())
                                    .doOnNext(event -> {
                                        // If reply-to is set we implicitly allow the subscriber to send a single message to the given destination
                                        // Reply-To is known to be scoped to the sender because there is a check when the system receives the event above
                                        // Ex:
                                        // Device -> subscribes to srv://MAC@device.rpc.channel
                                        // JS Client sends message to Device with a reply to of srv://CLIENT_ID@continuum.js.EventBus/replyHandler
                                        //
                                        // When the system receives the message in the send() handler above it verifies the reply-to matches the sender
                                        // Then we temporarily allow the device to send to the clients reply-to.
                                        // Which will allow the message to be routed back to the client.
                                        String replyTo = event.metadata().get(EventConstants.REPLY_TO_HEADER);
                                        if (replyTo != null) {
                                            // wildcard in the reply to are not allowed since they could bypass security constraints
                                            if (!replyTo.contains("*")) {
                                                session.addTemporarySendAllowed(replyTo);
                                            } else {
                                                log.warn("reply-to header contains * and will NOT be ALLOWED for message " + event);
                                            }
                                        }
                                    }) // services we want to make sure reply addresses are implicitly allowed
                                    .subscribe(subscriber);

            subscriptions.put(subscriptionIdentifier, subscriber);

            if (log.isDebugEnabled()) {
                log.debug("New subscription cri: " + cri.raw() + " id: " + subscriptionIdentifier + " for login: " + session.participant());
            }

        } else if (cri.scheme().equals(EventConstants.STREAM_DESTINATION_SCHEME)) {

            services.eventStreamService.listen(cri).subscribe(subscriber);

            subscriptions.put(subscriptionIdentifier, subscriber);

            if (log.isDebugEnabled()) {
                log.debug("New subscription cri: " + cri.raw() + " id: " + subscriptionIdentifier + " for login: " + session.participant());
            }

        } else {
            throw new IllegalArgumentException("CRI scheme not supported");
        }
    }


    public void unsubscribe(String subscriptionIdentifier) {
        Validate.notEmpty(subscriptionIdentifier, "subscriptionIdentifier must not be empty");

        if (subscriptions.containsKey(subscriptionIdentifier)) {
            subscriptions.remove(subscriptionIdentifier).cancel();
        } else {
            log.debug("No subscription exists for subscriptionIdentifier: " + subscriptionIdentifier);
        }

    }

    public void removeSession() {
        if (session != null) {
            services.sessionManager
                    .removeSession(session.sessionId())
                    .subscribe(value -> {
                                   if (!value) {
                                       log.error("Could not remove sessionId: " + session.sessionId());
                                   }
                               },
                               throwable -> log.error("Could not remove sessionId: " + session.sessionId(), throwable));
        } else {
            log.error("No session for connection was set");
        }
    }

    public void shutdown() {
        if (sessionTimer != -1) {
            services.vertx.cancelTimer(sessionTimer);
        }

        subscriptions.forEach((s, messageConsumer) -> messageConsumer.cancel());
        subscriptions.clear();
    }


}
