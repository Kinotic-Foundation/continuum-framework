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
import org.apache.commons.lang3.SystemUtils;
import org.apache.ignite.IgniteSystemProperties;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.DataRegionConfiguration;
import org.apache.ignite.configuration.DataStorageConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.events.EventType;
import org.apache.ignite.failure.FailureHandler;
import org.apache.ignite.failure.NoOpFailureHandler;
import org.apache.ignite.failure.StopNodeOrHaltFailureHandler;
import org.apache.ignite.logger.slf4j.Slf4jLogger;
import org.apache.ignite.spi.discovery.DiscoverySpi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.nio.file.Path;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

import static org.apache.ignite.failure.FailureType.*;

/**
 * Class provides environment agnostic configuration for ignite
 * Created by navid on 5/13/16.
 */
@Configuration
@ConditionalOnProperty(
        value="continuum.disableClustering",
        havingValue = "false",
        matchIfMissing = true)
public class ContinuumIgniteConfig {

    @Autowired
    private ContinuumProperties continuumProperties;

    @Autowired(required = false)
    private DiscoverySpi discoverySpi;

    @Autowired(required = false)
    private List<CacheConfiguration<?,?>> caches;

    @Autowired(required = false)
    private List<DataRegionConfiguration> dataRegions;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Bean
    public IgniteConfiguration igniteConfiguration(FailureHandler failureHandler) {
        // Setup a few system schema Ignite uses
        System.setProperty(IgniteSystemProperties.IGNITE_NO_ASCII, "true");// Turn off ignite console banner

        System.setProperty(IgniteSystemProperties.IGNITE_NO_SHUTDOWN_HOOK, "true");// keep from shutting down before our code

        // Opens H2 Debug Console in web browser on startup
        //System.setProperty(IgniteSystemProperties.IGNITE_H2_DEBUG_CONSOLE,"true");

        IgniteConfiguration cfg = new IgniteConfiguration();

        Path workPath = Path.of(SystemUtils.getUserHome().getAbsolutePath(),".continuum", "ignite", "work");
        cfg.setWorkDirectory(workPath.toAbsolutePath().toString());

        cfg.setGridLogger(new Slf4jLogger());

        // Turn Stuff OFF
        //cfg.setConnectorConfiguration(null) // REST Remote Management
        cfg.setMetricsLogFrequency(0);// Metrics Logging to Console

        // Override default discovery SPI.
        if(discoverySpi != null) {
            cfg.setDiscoverySpi(discoverySpi);
        }


        DataStorageConfiguration dataStorageConfiguration = new DataStorageConfiguration();

        // setup default memory region based on continuum config
        dataStorageConfiguration.getDefaultDataRegionConfiguration()
                                .setInitialSize(continuumProperties.getMaxOffHeapMemory() / 2)
                                .setMaxSize(continuumProperties.getMaxOffHeapMemory());

        if (dataRegions != null && !dataRegions.isEmpty()) {
            // Add other configured data regions
            DataRegionConfiguration[] configs = dataRegions.toArray(new DataRegionConfiguration[0]);
            dataStorageConfiguration.setDataRegionConfigurations(configs);
        }

        cfg.setDataStorageConfiguration(dataStorageConfiguration);


        // Ignite Cache configurations
        if (caches != null && !caches.isEmpty()) {
            CacheConfiguration<?,?>[] cacheConfigs = caches.toArray(new CacheConfiguration[0]);
            cfg.setCacheConfiguration(cacheConfigs);
        }

        // Settings needed for vertx cluster manager!
        cfg.setIncludeEventTypes(EventType.EVT_CACHE_OBJECT_REMOVED);

        cfg.setFailureHandler(failureHandler);

       // cfg.setPeerClassLoadingEnabled(true);

        return cfg;
    }

    @Bean
    @Profile({"development"})
    FailureHandler noopFailureHandler(){
        NoOpFailureHandler ret = new NoOpFailureHandler();
        ret.setIgnoredFailureTypes(Collections.unmodifiableSet(EnumSet.of(SEGMENTATION,
                                                                          SYSTEM_WORKER_TERMINATION,
                                                                          SYSTEM_WORKER_BLOCKED,
                                                                          CRITICAL_ERROR,
                                                                          SYSTEM_CRITICAL_OPERATION_TIMEOUT)));
        return ret;
    }

    @Bean
    @Profile("!development")
    FailureHandler haltFailureHandler(){
        return new StopNodeOrHaltFailureHandler();
    }

}
