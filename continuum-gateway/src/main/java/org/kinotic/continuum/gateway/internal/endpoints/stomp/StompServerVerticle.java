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

package org.kinotic.continuum.gateway.internal.endpoints.stomp;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.stomp.lite.StompServer;
import io.vertx.ext.stomp.lite.StompServerHandlerFactory;
import io.vertx.ext.stomp.lite.StompServerOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;
import org.kinotic.continuum.api.config.ContinuumProperties;
import org.kinotic.continuum.gateway.api.config.ContinuumGatewayProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 *
 * Created by Navid Mitchell on 2019-01-09.
 */
@Component
public class StompServerVerticle extends AbstractVerticle {

    private static final Logger log = LoggerFactory.getLogger(StompServerVerticle.class);

    private final ContinuumProperties continuumProperties;

    private final ContinuumGatewayProperties gatewayProperties;

    private final StompServerHandlerFactory stompServerHandlerFactory;

    private HttpServer httpServer;

    public StompServerVerticle(ContinuumProperties continuumProperties,
                               ContinuumGatewayProperties gatewayProperties,
                               StompServerHandlerFactory stompServerHandlerFactory) {
        this.continuumProperties = continuumProperties;
        this.gatewayProperties = gatewayProperties;
        this.stompServerHandlerFactory = stompServerHandlerFactory;
    }

    @Override
    public void start() {

        Router router = Router.router(vertx);
        router.route().handler(StaticHandler.create("continuum-gateway-static"));

        // FIXME: check CORS, see if it is protected or actually allowing any..?
        StompServerOptions stompServerOptions = gatewayProperties.getStomp();

        // we override the body length with the continuum properties
        stompServerOptions.setMaxBodyLength(continuumProperties.getMaxEventPayloadSize());
        HttpServerOptions serverOptions = new HttpServerOptions();
        serverOptions.setWebSocketSubProtocols(List.of("v12.stomp"));
        serverOptions.setMaxWebSocketFrameSize(continuumProperties.getMaxEventPayloadSize());

        httpServer = vertx.createHttpServer(serverOptions)
                          .webSocketHandler(StompServer.createWebSocketHandler(vertx,
                                                                               stompServerOptions,
                                                                               stompServerHandlerFactory))
                          .requestHandler(router)
                          .exceptionHandler(event -> log.error(
                                  "Stomp server Exception before completing Client Connection",
                                  event))
                          .listen(stompServerOptions.getPort(), stompServerOptions.getHost(), ar -> {
                              if (ar.succeeded()) {
                                  log.info("Stomp Server Listening on port "+ ar.result().actualPort());
                              } else {
                                  log.error("Error starting Stomp Server", ar.cause());
                              }
                          });

    }

    @Override
    public void stop() {
        httpServer.close();
    }
}

