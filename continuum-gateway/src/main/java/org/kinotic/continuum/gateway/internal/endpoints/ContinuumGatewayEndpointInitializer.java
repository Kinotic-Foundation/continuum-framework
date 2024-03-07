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

package org.kinotic.continuum.gateway.internal.endpoints;

import lombok.RequiredArgsConstructor;
import org.kinotic.continuum.gateway.internal.endpoints.rest.RestServerVerticle;
import org.kinotic.continuum.gateway.internal.endpoints.stomp.StompServerVerticle;
import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 *
 * Created by navid on 2/10/20
 */
@Component
@RequiredArgsConstructor
public class ContinuumGatewayEndpointInitializer {
    private static final Logger log = LoggerFactory.getLogger(ContinuumGatewayEndpointInitializer.class);

    private final StompServerVerticle stompServerVerticle;
    private final RestServerVerticle restServerVerticle;
    private final Environment environment;
    private final Vertx vertx;

    @PostConstruct
    public void init(){
        // If production deploy one verticle of each per core
        int numToDeploy = Math.min(environment.matchesProfiles("production") ? Math.max(Runtime.getRuntime().availableProcessors(), 1) : 1, 8);
        log.info(numToDeploy + " Cores will be used for Continuum Endpoints");

        log.info("Deploying Stomp Server Endpoint " + numToDeploy);
        vertx.deployVerticle(stompServerVerticle);

        log.info("Deploying REST Server Endpoint " + numToDeploy);
        vertx.deployVerticle(restServerVerticle);
    }

}
