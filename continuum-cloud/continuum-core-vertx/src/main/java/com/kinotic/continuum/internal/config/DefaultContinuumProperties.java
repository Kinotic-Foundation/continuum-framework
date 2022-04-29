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
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.ignite.configuration.DataStorageConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 *
 * Created by Navid Mitchell 🤪 on 10/24/19
 */
@Component
@ConfigurationProperties(prefix = "continuum")
public class DefaultContinuumProperties implements ContinuumProperties {

    public static String DEFAULT_KAFKA_BOOTSTRAP_SERVERS ="127.0.0.1:9092";
    public static String DEFAULT_ZOOKEEPER_SERVERS ="127.0.0.1:2181";
    public static long DEFAULT_SESSION_TIMEOUT = 1000 * 60 * 30;
    public static String DEFAULT_DISCOVERY = "sharedfs";

    private String kafkaBootstrapServers = DEFAULT_KAFKA_BOOTSTRAP_SERVERS;
    private String zookeeperServers = DEFAULT_ZOOKEEPER_SERVERS;
    private boolean debug = false;
    private boolean disableClustering = false;
    private int eventBusClusterPort = 0;
    private long sessionTimeout = DEFAULT_SESSION_TIMEOUT;
    private String discovery = DEFAULT_DISCOVERY;
    private long maxOffHeapMemory = DataStorageConfiguration.DFLT_DATA_REGION_MAX_SIZE;

    @Override
    public String getKafkaBootstrapServers() {
        return kafkaBootstrapServers;
    }

    public ContinuumProperties setKafkaBootstrapServers(String kafkaBootstrapServers) {
        this.kafkaBootstrapServers = kafkaBootstrapServers;
        return this;
    }

    @Override
    public String getZookeeperServers() {
        return zookeeperServers;
    }

    public ContinuumProperties setZookeeperServers(String zookeeperServers) {
        this.zookeeperServers = zookeeperServers;
        return this;
    }

    @Override
    public boolean isDebug() {
        return debug;
    }

    public ContinuumProperties setDebug(boolean debug) {
        this.debug = debug;
        return this;
    }

    @Override
    public boolean isDisableClustering() {
        return disableClustering;
    }

    public void setDisableClustering(boolean disableClustering) {
        this.disableClustering = disableClustering;
    }

    @Override
    public int getEventBusClusterPort() {
        return eventBusClusterPort;
    }

    public void setEventBusClusterPort(int eventBusClusterPort) {
        this.eventBusClusterPort = eventBusClusterPort;
    }

    @Override
    public long getSessionTimeout() {
        return sessionTimeout;
    }

    public ContinuumProperties setSessionTimeout(long sessionTimeout) {
        this.sessionTimeout = sessionTimeout;
        return this;
    }

    @Override
    public String getDiscovery() {
        return discovery;
    }

    /**
     * Sets the mode used for discovering other nodes within the cluster must be one of the following.
     * sharedfs : Uses TCP discovery with a shared filesystems
     * zookeeper : Uses Zookeeper discovery
     *
     * @param discovery the mode to use for discovery
     * @return this for fluent use
     */
    public ContinuumProperties setDiscovery(String discovery) {
        this.discovery = discovery;
        return this;
    }

    @Override
    public long getMaxOffHeapMemory() {
        return maxOffHeapMemory;
    }

    public ContinuumProperties setMaxOffHeapMemory(long maxOffHeapMemory) {
        this.maxOffHeapMemory = maxOffHeapMemory;
        return this;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("kafkaBootstrapServers", kafkaBootstrapServers)
                .append("zookeeperServers", zookeeperServers)
                .append("debug", debug)
                .append("disableClustering", disableClustering)
                .append("sessionTimeout", sessionTimeout)
                .append("discovery", discovery)
                .append("maxOffHeapMemory", maxOffHeapMemory)
                .toString();
    }
}
