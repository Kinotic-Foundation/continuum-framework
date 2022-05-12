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

package com.kinotic.continuum.internal.config;

import com.kinotic.continuum.api.config.ContinuumProperties;
import com.kinotic.continuum.internal.util.KafkaUtils;
import org.apache.commons.lang3.Validate;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import reactor.core.scheduler.Schedulers;
import reactor.kafka.sender.KafkaSender;

import java.util.HashMap;
import java.util.Map;

/**
 * Configures kafka using an explicit setup
 *
 * Created by navid on 11/19/19
 */
@Configuration
public class ContinuumKafkaConfig {

    @Autowired
    private ContinuumProperties continuumProperties;

    @Bean
    public KafkaAdmin admin() {
        Validate.notEmpty(continuumProperties.getKafkaBootstrapServers(), "Kafka bootstrap servers must be provided");
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, continuumProperties.getKafkaBootstrapServers());
        return new KafkaAdmin(configs);
    }

    private ProducerFactory<String, byte[]> kafkaProducerFactory() {
        return new DefaultKafkaProducerFactory<>(KafkaUtils.kafkaProducerConfigs(continuumProperties));
    }

    @Bean(destroyMethod = "close")
    public KafkaSender<String, byte[]> kafkaSender(){
        return KafkaUtils.createKafkaSender(continuumProperties, Schedulers.newParallel("kafkaSenderParallel"));
    }

    @Bean
    public KafkaTemplate<String, byte[]> kafkaTemplate() {
        return new KafkaTemplate<>(kafkaProducerFactory());
    }


}
