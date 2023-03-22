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

import org.kinotic.continuum.api.config.ContinuumProperties;
import io.vertx.core.Future;
import io.vertx.core.eventbus.EventBusOptions;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.spi.cluster.ignite.IgniteClusterManager;
import io.vertxbeans.VertxBeans;
import org.apache.ignite.Ignite;
import org.apache.ignite.internal.util.typedef.internal.U;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.ReactiveAdapterRegistry;
import org.springframework.core.ReactiveTypeDescriptor;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.util.function.Consumer;
import java.util.function.Supplier;


/**
 * Created by navid on 4/16/15.
 */
@Configuration
@Import(VertxBeans.class)
public class ContinuumVertxConfig {

    private static final Logger log = LoggerFactory.getLogger(ContinuumVertxConfig.class);

    @Bean
    @ConditionalOnProperty(
        value="continuum.disableClustering",
        havingValue = "false",
        matchIfMissing = true)
    public EventBusOptions eventBusOptions(ContinuumProperties properties){
        EventBusOptions ret = new EventBusOptions();
        ret.setPort(properties.getEventBusClusterPort());
        ret.setClustered(true); // This setting must be set again because in this scenario the setting below is overridden by the EventBusOptions here
                                // Eventually we should create our own impl of VertxBeans

        for(String ip : U.allLocalIps()){
            if(!ip.startsWith("169.254")){ // avoid binding to AWS internal net
                log.info("Setting vertx Cluster host to "+ip);
                ret.setHost(ip);
                break;
            }
        }
        return ret;
    }

    @Bean
    @ConditionalOnProperty(
        value="continuum.disableClustering",
        havingValue = "false",
        matchIfMissing = true)
    public ClusterManager clusterManager(Ignite ignite){
        if(ignite == null){
            throw new IllegalStateException("Something is wrong with the configuration Ignite is null");
        }
        // make sure clustering is enabled
        System.setProperty("vertx.clustered","true");

        return new IgniteClusterManager(ignite);
    }

    // This is configured in org.kinotic.continuum.internal.api.DefaultContinuum
    // It is done there in case this bean is supplied by spring directly
    @ConditionalOnMissingBean
    @Bean
    public ReactiveAdapterRegistry reactiveAdapterRegistry(){
        return new ReactiveAdapterRegistry();
    }

}
