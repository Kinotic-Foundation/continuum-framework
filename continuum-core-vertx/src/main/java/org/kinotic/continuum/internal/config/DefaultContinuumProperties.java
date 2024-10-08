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

package org.kinotic.continuum.internal.config;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.kinotic.continuum.api.config.ContinuumProperties;
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
@Getter
@Setter
@Accessors(chain = true)
public class DefaultContinuumProperties implements ContinuumProperties {

    public static long DEFAULT_SESSION_TIMEOUT = 1000 * 60 * 30;
    public static String DEFAULT_DISCOVERY = "sharedfs";

    private boolean debug = false;
    private boolean disableClustering = false;
    private int eventBusClusterPort = 0;
    private long sessionTimeout = DEFAULT_SESSION_TIMEOUT;
    /**
     * Sets the mode used for discovering other nodes within the cluster must be one of the following.
     * sharedfs : Uses TCP discovery with a shared filesystems
     * zookeeper : Uses Zookeeper discovery
     */
    private String discovery = DEFAULT_DISCOVERY;
    private long maxOffHeapMemory = DataStorageConfiguration.DFLT_DATA_REGION_MAX_SIZE;
    private int maxEventPayloadSize = 1024 * 1024 * 100; // 100MB
    private int maxNumberOfCoresToUse = Math.max(Runtime.getRuntime().availableProcessors(), 1);


    public DefaultContinuumProperties setMaxNumberOfCoresToUse(int maxNumberOfCoresToUse) {
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        this.maxNumberOfCoresToUse = maxNumberOfCoresToUse > 0 ? Math.min(availableProcessors, maxNumberOfCoresToUse) : Math.max(availableProcessors, 1);
        return this;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("debug", debug)
                .append("disableClustering", disableClustering)
                .append("sessionTimeout", sessionTimeout)
                .append("discovery", discovery)
                .append("maxOffHeapMemory", maxOffHeapMemory)
                .toString();
    }
}
