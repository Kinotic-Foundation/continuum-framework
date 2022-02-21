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

package com.kinotic.continuum.internal.api;

import com.kinotic.continuum.api.Continuum;
import com.kinotic.continuum.api.config.ContinuumProperties;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.spi.cluster.ClusterManager;
import org.apache.commons.lang3.text.WordUtils;
import org.apache.ignite.internal.util.typedef.internal.U;
import org.awaitility.Awaitility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

/**
 *
 * Created by navid on 9/24/19
 */
@Component
public class DefaultContinuum implements Continuum {

    private static final Logger log = LoggerFactory.getLogger(Continuum.class);
    private static final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy HH:mm:ss z");
    private static final int ADJECTIVE_COUNT = 1915;
    private static final int ANIMAL_COUNT = 587;
    private final String name;
    private final ContinuumProperties continuumProperties;
    private final Vertx vertx;
    private final String nodeId;

    public DefaultContinuum(ResourceLoader resourceLoader,
                            @Autowired(required = false)
                            ClusterManager clusterManager,
                            Vertx vertx,
                            ContinuumProperties continuumProperties) throws IOException {
        String name;

        try (Stream<String> fileStream = new BufferedReader(new InputStreamReader(resourceLoader.getResource("classpath:adjectives.txt").getInputStream())).lines()) {
            name = fileStream.skip(getRandomNumberInRange(ADJECTIVE_COUNT))
                             .findFirst()
                             .orElse("");
        }

        try (Stream<String> fileStream = new BufferedReader(new InputStreamReader(resourceLoader.getResource("classpath:animals.txt").getInputStream())).lines()) {
            String temp = fileStream.skip(getRandomNumberInRange(ANIMAL_COUNT))
                                    .findFirst()
                                    .orElse("");
            name = name + " " + WordUtils.capitalize(temp);
        }
        this.name = name;
        this.vertx = vertx;
        this.continuumProperties = continuumProperties;
        this.nodeId = (clusterManager != null  ?  clusterManager.getNodeID() : UUID.randomUUID().toString());
    }

    @Override
    public String nodeName() {
        return name;
    }

    @Override
    public String nodeId() {
        return nodeId;
    }

    @Override
    public String applicationName() {
        return "AddMe";
    }

    @EventListener
    public void onApplicationReadyEvent(ApplicationReadyEvent event) {
        StringBuilder info = new StringBuilder("\n\n##### Continuum Process Started #####\n\n\t");
        info.append(name);
        info.append("\n\tNode Id: ");
        info.append(nodeId);
        info.append("\n\t");
        info.append(sdf.format(new Date()));
        info.append("\n\n\tHost IPs:");
        for(String ip : U.allLocalIps()){
            info.append("\n\t\t");
            info.append(ip);
        }
        info.append("\n\n");
        info.append(continuumProperties.toString());

        log.info(info.toString());
    }

    @PreDestroy
    public void shutdown(){
        Promise<Void> promise = Promise.promise();
        vertx.close(promise);
        Awaitility.await().atMost(2, TimeUnit.MINUTES).until(() -> promise.future().isComplete());
    }


    @SuppressWarnings("OptionalGetWithoutIsPresent")
    private static int getRandomNumberInRange(int max) {
        Random r = new Random();
        return r.ints(0, (max + 1)).findFirst().getAsInt();
    }
}
