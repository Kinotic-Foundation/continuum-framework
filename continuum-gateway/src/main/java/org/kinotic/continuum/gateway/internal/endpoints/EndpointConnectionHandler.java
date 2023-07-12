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

import com.fasterxml.jackson.core.JsonProcessingException;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.ReplyException;
import io.vertx.core.eventbus.ReplyFailure;
import org.apache.commons.lang3.Validate;
import org.kinotic.continuum.api.exceptions.RpcMissingServiceException;
import org.kinotic.continuum.api.exceptions.AuthenticationException;
import org.kinotic.continuum.api.exceptions.AuthorizationException;
import org.kinotic.continuum.api.security.ConnectedInfo;
import org.kinotic.continuum.api.security.SecurityService;
import org.kinotic.continuum.core.api.event.CRI;
import org.kinotic.continuum.core.api.event.Event;
import org.kinotic.continuum.core.api.event.EventConstants;
import org.kinotic.continuum.core.api.security.Session;
import org.kinotic.continuum.gateway.internal.api.security.CliSecurityService;
import org.kinotic.continuum.internal.utils.ContinuumUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;

/**
 * Generic class to perform {@link Event} handling coming from various endpoints
 * <p>
 * <p>
 * Created by Navid Mitchell on 11/3/20
 */
public class EndpointConnectionHandler {

    private static final Logger log = LoggerFactory.getLogger(EndpointConnectionHandler.class);

    private final Vertx vertx;
    private final Services services;
    private final SecurityService securityService;

    private final Map<String, BaseSubscriber<Event<byte[]>>> subscriptions = new HashMap<>();
    private Session session;
    private long sessionTimer = -1;

    public EndpointConnectionHandler(Vertx vertx,
                                     Services services) {
        this.vertx = vertx;
        this.services = services;

        if(services.continuumGatewayProperties.isEnableCLIConnections()){
            this.securityService = new CliSecurityService(services.securityService);
        }else{
            this.securityService = services.securityService;
        }
    }

    /**
     * Requests authentication for the given credentials
     * @param connectHeaders all the headers provided with the CONNECT frame. This will include the login and passcode headers.
     * @return a {@link Promise} completed normally to authenticate or failed to represent a failed authentication
     *         The promise must contain a Map that will provide any additional headers to be returned to the client with the CONNECTED frame
     */
    public CompletableFuture<Map<String, String>> authenticate(Map<String, String> connectHeaders) {
        // Check if session is being used to authenticate
        if (connectHeaders.containsKey(EventConstants.SESSION_HEADER)) {

            String sessionId = connectHeaders.get(EventConstants.SESSION_HEADER);
            return services.sessionManager
                    .findSession(sessionId)
                    .handle((session, throwable) -> {
                        if(throwable != null){
                            throw new AuthenticationException("Could not authenticate with the given Session id", throwable);
                        }else{
                            sessionActive(session);
                            return Map.of(EventConstants.CONNECTED_INFO_HEADER, createConnectedInfoJson(session));
                        }
                    });
        } else {

            return securityService.authenticate(connectHeaders)
                                  .handle((participant, throwable) -> {
                                      if(throwable != null){
                                          if(!(throwable instanceof AuthenticationException)) {
                                              throw new AuthenticationException("Could not authenticate with the given credentials", throwable);
                                          }else{
                                              throw (AuthenticationException) throwable;
                                          }
                                      }else {
                                          return participant;
                                      }
                                  })
                                  .thenCompose(participant -> services.sessionManager.create(participant))
                                  .thenApply(session -> {
                                      sessionActive(session);
                                      return Map.of(EventConstants.CONNECTED_INFO_HEADER, createConnectedInfoJson(session));
                                  });
        }
    }

    private String createConnectedInfoJson(Session session){
        try {
            ConnectedInfo connectedInfo = new ConnectedInfo(session.sessionId(), session.participant());
            return services.objectMapper.writeValueAsString(connectedInfo);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
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

            if (event.cri().scheme().equals(EventConstants.SERVICE_DESTINATION_SCHEME)) {

                try {

                    event.metadata().put(EventConstants.SENDER_HEADER, services.objectMapper.writeValueAsString(session.participant()));

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
            ret = Mono.error(new AuthorizationException("Not Authorized to send to " + event.cri()));
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
            throw new AuthorizationException("Not Authorized to subscribe to " + cri);
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
                    .handle((BiFunction<Boolean, Throwable, Void>) (aBoolean, throwable) -> {
                        if (throwable != null) {
                            log.error("Could not remove sessionId: " + session.sessionId(), throwable);
                        }
                        return null;
                    });
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
