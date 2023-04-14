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

package org.kinotic.continuum.internal.utils;

import org.kinotic.continuum.api.config.ContinuumProperties;
import org.apache.commons.lang3.Validate;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import reactor.core.scheduler.Scheduler;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by 🤓 on 6/17/21.
 */
public class KafkaUtils {

    public static KafkaSender<String, byte[]> createKafkaSender(ContinuumProperties continuumProperties){
        return createKafkaSender(continuumProperties, null);
    }

    public static KafkaSender<String, byte[]> createKafkaSender(ContinuumProperties continuumProperties, Scheduler scheduler){
        SenderOptions<String, byte[]> senderOptions = SenderOptions.create(kafkaProducerConfigs(continuumProperties));
        if(scheduler != null){
            senderOptions.scheduler(scheduler);
        }
        return KafkaSender.create(senderOptions);
    }

    public static Map<String, Object> kafkaProducerConfigs(ContinuumProperties continuumProperties) {
        Validate.notEmpty(continuumProperties.getKafkaBootstrapServers(), "Kafka bootstrap servers must be provided");
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, continuumProperties.getKafkaBootstrapServers());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class);

        /*
         * acks=all This means the leader will wait for the full set of in-sync replicas to acknowledge the record.
         * This guarantees that the record will not be lost as long as at least one in-sync replica remains alive.
         * This is the strongest available guarantee. This is equivalent to the acks=-1 setting.
         */
        props.put(ProducerConfig.ACKS_CONFIG, "all");

        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");

        // See https://kafka.apache.org/documentation/#producerconfigs for more properties
        return props;
    }

}
