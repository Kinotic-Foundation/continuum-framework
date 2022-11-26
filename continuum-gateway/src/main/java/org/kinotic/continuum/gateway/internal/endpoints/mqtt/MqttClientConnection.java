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

import io.netty.handler.codec.mqtt.MqttQoS;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.SocketAddress;
import io.vertx.mqtt.MqttEndpoint;

import javax.net.ssl.SSLSession;
import java.util.List;

/**
 *
 * Created by Navid Mitchell on 11/10/20
 */
public class MqttClientConnection {

    private final MqttEndpoint mqttEndpoint;

    public MqttClientConnection(MqttEndpoint mqttEndpoint) {
        this.mqttEndpoint = mqttEndpoint;
    }
    
    public SocketAddress remoteAddress() {
        return mqttEndpoint.remoteAddress();
    }
    
    public SocketAddress localAddress() {
        return mqttEndpoint.localAddress();
    }

    public boolean isSsl() {
        return mqttEndpoint.isSsl();
    }
    
    public SSLSession sslSession() {
        return mqttEndpoint.sslSession();
    }
    
    public String clientIdentifier() {
        return mqttEndpoint.clientIdentifier();
    }
    
    public int protocolVersion() {
        return mqttEndpoint.protocolVersion();
    }
    
    public String protocolName() {
        return mqttEndpoint.protocolName();
    }
    
    public boolean isCleanSession() {
        return mqttEndpoint.isCleanSession();
    }
    
    public int keepAliveTimeSeconds() {
        return mqttEndpoint.keepAliveTimeSeconds();
    }
    
    public int lastMessageId() {
        return mqttEndpoint.lastMessageId();
    }

    public MqttClientConnection subscribeAcknowledge(int subscribeMessageId, List<MqttQoS> grantedQoSLevels) {
        mqttEndpoint.subscribeAcknowledge(subscribeMessageId, grantedQoSLevels);
        return this;
    }


    public MqttClientConnection unsubscribeAcknowledge(int unsubscribeMessageId) {
        mqttEndpoint.unsubscribeAcknowledge(unsubscribeMessageId);
        return this;
    }


    public MqttClientConnection publishAcknowledge(int publishMessageId) {
        mqttEndpoint.publishAcknowledge(publishMessageId);
        return this;
    }


    public MqttClientConnection publishReceived(int publishMessageId) {
        mqttEndpoint.publishReceived(publishMessageId);
        return this;
    }


    public MqttClientConnection publishRelease(int publishMessageId) {
        mqttEndpoint.publishRelease(publishMessageId);
        return this;
    }


    public MqttClientConnection publishComplete(int publishMessageId) {
        mqttEndpoint.publishComplete(publishMessageId);
        return this;
    }


    public MqttClientConnection publish(String topic,
                                Buffer payload,
                                MqttQoS qosLevel, boolean isDup, boolean isRetain) {
        mqttEndpoint.publish(topic, payload, qosLevel, isDup, isRetain);
        return this;
    }


    public MqttClientConnection publish(String topic,
                                Buffer payload,
                                MqttQoS qosLevel,
                                boolean isDup,
                                boolean isRetain,
                                Handler<AsyncResult<Integer>> publishSentHandler) {
        mqttEndpoint.publish(topic, payload, qosLevel, isDup, isRetain, publishSentHandler);
        return this;
    }


    public MqttClientConnection publish(String topic,
                                Buffer payload,
                                MqttQoS qosLevel,
                                boolean isDup,
                                boolean isRetain,
                                int messageId,
                                Handler<AsyncResult<Integer>> publishSentHandler) {
        mqttEndpoint.publish(topic, payload, qosLevel, isDup, isRetain, messageId, publishSentHandler);
        return this;
    }

    public void close() {
        mqttEndpoint.close();
    }

}
