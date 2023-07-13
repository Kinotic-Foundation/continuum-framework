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

package org.kinotic.continuum.gateway.internal.endpoints.stomp;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.ext.stomp.lite.StompServerConnection;
import io.vertx.ext.stomp.lite.StompServerHandler;
import io.vertx.ext.stomp.lite.frame.Frame;
import io.vertx.ext.stomp.lite.frame.InvalidConnectFrame;
import org.kinotic.continuum.api.exceptions.AuthorizationException;
import org.kinotic.continuum.core.api.event.CRI;
import org.kinotic.continuum.core.api.event.Event;
import org.kinotic.continuum.core.api.event.EventBusService;
import org.kinotic.continuum.gateway.internal.endpoints.EndpointConnectionHandler;
import org.kinotic.continuum.gateway.internal.endpoints.Services;
import org.kinotic.continuum.internal.core.api.service.invoker.ExceptionConverter;
import org.kinotic.continuum.internal.utils.EventUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.util.Map;

/**
 *
 * Created by Navid Mitchell on 2019-02-05.
 */
public class DefaultStompServerHandler implements StompServerHandler {

    private static final Logger log = LoggerFactory.getLogger(DefaultStompServerHandler.class);

    private final Vertx vertx;
    private final StompServerConnection connection;
    private final ExceptionConverter exceptionConverter;
    private final EventBusService eventBusService;
    private final EndpointConnectionHandler endpointConnectionHandler;


    public DefaultStompServerHandler(Vertx vertx,
                                     Services services,
                                     StompServerConnection connection) {
        this.vertx = vertx;
        this.connection = connection;
        this.exceptionConverter = services.exceptionConverter;
        this.eventBusService = services.eventBusService;
        this.endpointConnectionHandler = new EndpointConnectionHandler(vertx, services);
    }

    @Override
    public Promise<Map<String, String>> authenticate(Map<String, String> connectHeaders) {
        Promise<Map<String,String>> ret = Promise.promise();
        Future<Map<String,String>> future = Future.fromCompletionStage(endpointConnectionHandler.authenticate(connectHeaders),
                                                                       vertx.getOrCreateContext());
        future.onComplete(ret);
        return ret;
    }

    @Override
    public void send(Frame frame) {
        // We pause the client to effectively make all client requests block until the previous request is handled asynchronously
        connection.pause();

        if(log.isTraceEnabled()) {
            log.trace("Frame received\n" + frame.toString());
        }

        Event<byte[]> incomingEvent = new FrameEventAdapter(frame);

        endpointConnectionHandler
                .send(incomingEvent)
                .subscribe(null,
                           throwable -> {
                               if(throwable instanceof AuthorizationException){
                                   connection.sendErrorAndDisconnect(throwable);
                               }else{
                                   try {
                                       if(log.isDebugEnabled()){
                                           log.debug("Exception occurred processing service request\n" + EventUtil.toString(incomingEvent, true), throwable);
                                       }

                                       Event<byte[]> convertedEvent = exceptionConverter.convert(incomingEvent.metadata(), throwable);
                                       // since we don't know the subscription id used by the stomp client for this request we send through the eventbus
                                       eventBusService.send(convertedEvent);
                                       connection.resume();
                                   } catch (Exception ex) {
                                       log.error("Error occurred when calling exception converter", throwable);
                                       connection.sendErrorAndDisconnect(throwable);
                                   }
                               }
                           },
                           () -> {
                               connection.sendReceiptIfNeeded(frame);
                               connection.resume();
                           });
    }

    @Override
    public void subscribe(Frame frame) {
        if(log.isTraceEnabled()) {
            log.trace("Frame received\n" + frame.toString());
        }

        try {

            String subscriptionId = frame.getHeader(Frame.ID);
            Assert.hasText(subscriptionId,"Subscription requests must contain an Id header");

            CRI cri = CRI.create(frame.getDestination());

            StompSubscriptionEventSubscriber subscriber = new StompSubscriptionEventSubscriber(cri.raw(), subscriptionId, connection);
            endpointConnectionHandler.subscribe(cri, subscriptionId, subscriber);

        } catch (Exception e) {
            log.error("Exception occurred handling subscribe", e);
            connection.sendErrorAndDisconnect(e);
        }
    }

    @Override
    public void unsubscribe(Frame frame) {
        if(log.isTraceEnabled()) {
            log.trace("Frame received\n" + frame.toString());
        }

        try {
            String subscriptionId = frame.getHeader(Frame.ID);

            endpointConnectionHandler.unsubscribe(subscriptionId);

        } catch (Exception e) {
            log.error("Exception occurred handling unsubscribe", e);
            connection.sendErrorAndDisconnect(e);
        }
    }

    @Override
    public void begin(Frame frame) {
        log.debug("Frame received\n" + frame.toString());
    }

    @Override
    public void abort(Frame frame) {
        log.debug("Frame received\n" + frame.toString());
    }

    @Override
    public void commit(Frame frame) {
        log.debug("Frame received\n" + frame.toString());
    }

    @Override
    public void ack(Frame frame) {
        log.debug("Frame received\n" + frame.toString());
    }

    @Override
    public void nack(Frame frame) {
        log.debug("Frame received\n" + frame.toString());
    }

    @Override
    public void exception(Throwable t) {
        // TODO: Add support for auto blacklisting client
        if(t instanceof InvalidConnectFrame){
            log.error("Invalid connect frame "+t.getMessage()+"\nFrame: "+((InvalidConnectFrame)t).getData().toString());
        }else{
            log.error("Client Caused Exception", t);
        }
    }

    @Override
    public void disconnected() {
        endpointConnectionHandler.removeSession();
    }

    @Override
    public void closed() {
        endpointConnectionHandler.shutdown();
    }

}
