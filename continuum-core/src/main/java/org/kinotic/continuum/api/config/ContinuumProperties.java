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

package org.kinotic.continuum.api.config;

/**
 *
 * Created by Navíd Mitchell 🤪 on 1/18/21
 */
public interface ContinuumProperties {

    String getKafkaBootstrapServers();

    String getZookeeperServers();

    /**
     * If true additional information will be provided to clients,
     * including server information, and information about errors occurring when invoking services
     * This is off by default since this could reveal server implementation details
     * @return true to enable debug mode false to disable it
     */
    boolean isDebug();

    /**
     * @return true if clustering should be disabled false if not
     */
    boolean isDisableClustering();

    /**
     * @return the port to use when clustering = 0 (meaning assign a random port)
     */
    int getEventBusClusterPort();

    long getSessionTimeout();

    /**
     * Determines the mode used for discovering other nodes within the cluster must be one of the following.
     * sharedfs : Uses TCP discovery with a shared filesystems. This only works when all nodes run on the same host.
     * zookeeper : Uses Zookeeper discovery
     * multicast : Uses multicast discovery
     *
     * @return the discovery mode
     */
    String getDiscovery();

    long getMaxOffHeapMemory();

    int getMaxEventPayloadSize();

    /**
     * The maximum number of CPU cores if not set or less than 1, this will default to the available number of cores.
     * @return the max number of CPU Cores to Use
     */
    int getMaxNumberOfCoresToUse();
}
