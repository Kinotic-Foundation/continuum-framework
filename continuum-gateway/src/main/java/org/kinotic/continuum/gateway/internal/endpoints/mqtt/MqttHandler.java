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

package org.kinotic.continuum.gateway.internal.endpoints.mqtt;

import org.kinotic.continuum.gateway.internal.endpoints.EndpointConnectionHandler;
import io.vertx.core.Promise;
import io.vertx.mqtt.messages.MqttPublishMessage;
import io.vertx.mqtt.messages.MqttSubscribeMessage;
import io.vertx.mqtt.messages.MqttUnsubscribeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 *
 * Created by Navid Mitchell on 11/6/20
 */
public class MqttHandler {
    private static final Logger log = LoggerFactory.getLogger(MqttHandler.class);

    private final EndpointConnectionHandler endpointConnectionHandler;
    private final MqttClientConnection clientConnection;

    public MqttHandler(EndpointConnectionHandler endpointConnectionHandler,
                       MqttClientConnection clientConnection) {
        this.endpointConnectionHandler = endpointConnectionHandler;
        this.clientConnection = clientConnection;
    }

    public Promise<Map<String, String>> authenticate(String identity, String secret){
        return null;
    }

    public void publish(MqttPublishMessage message){

    }

    public void publishAcknowledge(Integer messageId){

    }

    public void publishReceived(Integer messageId){

    }

    public void publishCompletion(Integer messageId){

    }

    public void subscribe(MqttSubscribeMessage subscribeMessage){

    }

    public void unsubscribe(MqttUnsubscribeMessage unsubscribeMessage){

    }

    public void exception(Throwable throwable){

    }

    public void close(){
        endpointConnectionHandler.shutdown();
    }

}
