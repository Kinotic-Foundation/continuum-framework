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

import org.kinotic.continuum.core.api.event.CRI;
import org.kinotic.continuum.core.api.event.Event;
import org.kinotic.continuum.core.api.event.Metadata;
import io.vertx.mqtt.messages.MqttPublishMessage;

/**
 *
 * Created by Navid Mitchell on 11/11/20
 */
public class MqttPublishMessageEventAdapter implements Event<byte[]> {

    private final MqttPublishMessage mqttPublishMessage;
    private final CRI cri;
    private final Metadata metadata;

    public MqttPublishMessageEventAdapter(MqttPublishMessage mqttPublishMessage) {
        this.mqttPublishMessage = mqttPublishMessage;
        this.cri = CRI.create(mqttPublishMessage.topicName());
        this.metadata = Metadata.create();
    }

    @Override
    public CRI cri() {
        return cri;
    }

    @Override
    public Metadata metadata() {
        return metadata;
    }

    @Override
    public byte[] data() {
        return mqttPublishMessage.payload().getBytes();
    }
}
