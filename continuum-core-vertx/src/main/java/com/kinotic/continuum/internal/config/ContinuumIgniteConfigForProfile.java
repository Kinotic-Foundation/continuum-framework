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
import com.kinotic.continuum.core.api.security.SessionMetadata;
import com.kinotic.continuum.internal.core.api.security.DefaultSessionMetadata;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.cache.CacheWriteSynchronizationMode;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.internal.util.typedef.F;
import org.apache.ignite.internal.util.typedef.internal.U;
import org.apache.ignite.spi.discovery.DiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.multicast.TcpDiscoveryMulticastIpFinder;
import org.apache.ignite.spi.discovery.tcp.ipfinder.sharedfs.TcpDiscoverySharedFsIpFinder;
import org.apache.ignite.spi.discovery.zk.ZookeeperDiscoverySpi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.cache.expiry.Duration;
import javax.cache.expiry.TouchedExpiryPolicy;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 * Created by navid on 5/13/16.
 */
@Configuration
@ConditionalOnProperty(
        value="continuum.disableClustering",
        havingValue = "false",
        matchIfMissing = true)
public class ContinuumIgniteConfigForProfile {

    // NOTE: Key is the session id
    public static final String SESSION_CACHE_NAME = "__continuumSessionCache";

    @Autowired
    private ContinuumProperties continuumProperties;

    @Bean
    public DiscoverySpi tcpDiscoverySpi() {
        DiscoverySpi ret;
        if(continuumProperties.getDiscovery().equals("sharedfs")){
            TcpDiscoverySharedFsIpFinder finder = new TcpDiscoverySharedFsIpFinder();
            TcpDiscoverySpi spi = new TcpDiscoverySpi();
            spi.setIpFinder(finder);
            ret = spi;
        }else if(continuumProperties.getDiscovery().equals("zookeeper")) {
            ZookeeperDiscoverySpi spi = new ZookeeperDiscoverySpi();
            spi.setZkConnectionString(continuumProperties.getZookeeperServers());
            spi.setSessionTimeout(90000);
            spi.setJoinTimeout(30000);
            ret = spi;
        }else if(continuumProperties.getDiscovery().equals("multicast")){
            TcpDiscoveryMulticastIpFinder tcpDiscoveryMulticastIpFinder = new TcpDiscoveryMulticastIpFinder();
            tcpDiscoveryMulticastIpFinder.setAddresses(Collections.singleton("127.0.0.1"));
            TcpDiscoverySpi spi = new TcpDiscoverySpi();
            spi.setIpFinder(tcpDiscoveryMulticastIpFinder);
            //Loopback multicast discovery is not working on Mac OS
            //(possibly due to http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=7122846).
            if (U.isMacOs()) {
                spi.setLocalAddress(F.first(U.allLocalIps()));
            }
            ret = spi;
        }else{
            throw new IllegalStateException("Unknown discovery setting "+continuumProperties.getDiscovery());
        }
        return ret;
    }

    @Bean
    public CacheConfiguration<String, SessionMetadata> sessionCache(){
        // NOTE: Key is the session id
        CacheConfiguration<String, SessionMetadata> cacheConfiguration = new CacheConfiguration<>();
        cacheConfiguration.setName(SESSION_CACHE_NAME);
        cacheConfiguration.setCacheMode(CacheMode.PARTITIONED);
        cacheConfiguration.setBackups(1);
        cacheConfiguration.setWriteSynchronizationMode(CacheWriteSynchronizationMode.PRIMARY_SYNC);
        cacheConfiguration.setExpiryPolicyFactory(TouchedExpiryPolicy.factoryOf(new Duration(TimeUnit.MILLISECONDS,
                                                                                             continuumProperties.getSessionTimeout())));
        cacheConfiguration.setSqlSchema("PUBLIC");
        cacheConfiguration.setIndexedTypes(String.class, DefaultSessionMetadata.class);

        return cacheConfiguration;
    }

}
