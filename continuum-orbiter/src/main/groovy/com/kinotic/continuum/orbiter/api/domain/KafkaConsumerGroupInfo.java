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

package com.kinotic.continuum.orbiter.api.domain;

import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.kafka.common.ConsumerGroupState;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by Navid Mitchell on 11/25/20
 */
@Data
@Accessors(chain = true)
public class KafkaConsumerGroupInfo {

    private String groupId;
    private ConsumerGroupState groupState;
    private String coordinator;
    private String partitionAssignor;
    private List<KafkaConsumerInstanceInfo> consumerInstances = new ArrayList<>();
    private boolean monitored;

    public KafkaConsumerGroupInfo() {
    }

    public KafkaConsumerGroupInfo(String groupId) {
        this.groupId = groupId;
    }
}
