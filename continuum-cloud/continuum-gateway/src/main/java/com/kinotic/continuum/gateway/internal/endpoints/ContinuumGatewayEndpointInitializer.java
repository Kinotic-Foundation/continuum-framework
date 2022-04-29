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

package com.kinotic.continuum.gateway.internal.endpoints;

import com.kinotic.continuum.gateway.internal.endpoints.rest.RestServerVerticle;
import com.kinotic.continuum.gateway.internal.endpoints.stomp.StompServerVerticle;
import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 *
 * Created by navid on 2/10/20
 */
@Component
public class ContinuumGatewayEndpointInitializer {
    private static final Logger log = LoggerFactory.getLogger(ContinuumGatewayEndpointInitializer.class);

    @Autowired
    private Vertx vertx;

    /**
     * Autowire all endpoint below.
     * This is done one by one instead of all Verticles since we do not want clients to have side effects..
     */
    @Autowired
    private StompServerVerticle stompServerVerticle;

    @Autowired
    private RestServerVerticle restServerVerticle;

    @PostConstruct
    public void init(){
        log.info("Deploying Stomp Server Endpoint");
        vertx.deployVerticle(stompServerVerticle);

        // TODO: Finish security impl in verticle prior to enabling
        //log.info("Deploying REST Server Endpoint");
        //vertx.deployVerticle(restServerVerticle);
    }

}
