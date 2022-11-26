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

import org.kinotic.continuum.core.api.event.CRI;
import org.kinotic.continuum.core.api.event.EventConstants;
import org.kinotic.continuum.gateway.internal.endpoints.EndpointConnectionHandler;
import org.kinotic.continuum.gateway.internal.endpoints.Services;
import org.kinotic.continuum.internal.utils.ContinuumUtil;
import io.vertx.core.Promise;
import io.vertx.ext.stomp.lite.StompServerConnection;
import io.vertx.ext.stomp.lite.StompServerHandler;
import io.vertx.ext.stomp.lite.frame.Frame;
import io.vertx.ext.stomp.lite.frame.InvalidConnectFrame;
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

    private final Services services;
    private final StompServerConnection connection;
    private String encodedIdentity;
    private boolean translateDestination = false;

    private final EndpointConnectionHandler endpointConnectionHandler;

    public DefaultStompServerHandler(Services services,
                                     StompServerConnection connection) {
        this.services = services;
        this.connection = connection;
        this.endpointConnectionHandler = new EndpointConnectionHandler(services);
    }

    @Override
    public Promise<Map<String, String>> authenticate(Map<String, String> connectHeaders) {
        Promise<Map<String, String>> ret;
        if(connectHeaders.containsKey(Frame.LOGIN)) {

            String identity = connectHeaders.get(Frame.LOGIN);
            encodedIdentity = ContinuumUtil.safeEncodeURI(identity);

            // This logic is only needed by legacy devices it can go away once they are all upgraded
            String clientVersion = connectHeaders.get("app.version");
            if (clientVersion != null && !clientVersion.startsWith("3")) {
                translateDestination = true;
            }

            ret = endpointConnectionHandler.authenticate(connectHeaders);

        }else if (connectHeaders.containsKey(EventConstants.SESSION_HEADER)){
            ret = endpointConnectionHandler.authenticate(connectHeaders);
        }else{
            ret = Promise.promise();
            ret.fail("The connection frame does not contain valid credentials");
        }
        return ret;
    }

    @Override
    public void send(Frame frame) {
        // We pause the client to effectively make all client requests block until the previous request is handled asynchronously
        connection.pause();

        if(log.isTraceEnabled()) {
            log.trace("Frame received\n" + frame.toString());
        }

        translateDestinationIfNeeded(frame);

        endpointConnectionHandler.send(new FrameEventAdapter(frame))
                                 .subscribe(null,
                                            throwable -> {
                                                connection.sendErrorAndDisconnect(throwable);
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
            translateDestinationIfNeeded(frame);

            String subscriptionId = frame.getHeader(Frame.ID);
            Assert.hasText(subscriptionId,"Subscription requests must contain an Id header");

            CRI cri = CRI.create(frame.getDestination());

            StompSubscriptionEventSubscriber subscriber = new StompSubscriptionEventSubscriber(cri.raw(), subscriptionId, connection);
            endpointConnectionHandler.subscribe(cri, subscriptionId, subscriber);

        } catch (Exception e) {
            log.error("Exception occurred handling subscribe", e);
            connection.sendError(e); // we don't disconnect since not fatal
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
            connection.sendError(e); // we don't disconnect since not fatal
        }
    }


    public void translateDestinationIfNeeded(Frame frame){
        if(translateDestination){
            String destination = frame.getDestination();

            // first short circuit if destination is already correct format
            if(destination.startsWith("srv://") || destination.startsWith("stream://")){
                return;
            }

            // translate any v2 destination to a compatible v3 destination
            if (destination.startsWith("bus/device/rpc/")){
                if(destination.length() != 27){
                    throw new IllegalArgumentException("Invalid destination. Bye!");
                }
                String deviceMac = destination.substring(15);
                frame.getHeaders().put(Frame.DESTINATION, "srv://"+deviceMac+"@continuum.cpp.RpcService");
            } else if(destination.startsWith("bus/")){
                if(destination.length() < 5){
                    throw new IllegalArgumentException("Invalid destination. Bye!");
                }
                frame.getHeaders().put(Frame.DESTINATION, "srv://"+destination.substring(4));
            } else if(destination.startsWith("stomp/")) {
                throw new IllegalArgumentException("Invalid destination. Bye!");
            } else{
                frame.getHeaders().put(Frame.DESTINATION, "stream://"+encodedIdentity+"@"+frame.getDestination());
            }
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
