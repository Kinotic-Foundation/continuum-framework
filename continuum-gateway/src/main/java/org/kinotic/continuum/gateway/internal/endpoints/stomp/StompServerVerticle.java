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

import io.vertx.ext.web.handler.StaticHandler;
import org.kinotic.continuum.gateway.api.config.ContinuumGatewayProperties;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.stomp.lite.StompServer;
import io.vertx.ext.stomp.lite.StompServerHandlerFactory;
import io.vertx.ext.stomp.lite.StompServerOptions;
import io.vertx.ext.web.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 *
 * Created by Navid Mitchell on 2019-01-09.
 */
@Component
public class StompServerVerticle extends AbstractVerticle {

    private static final Logger log = LoggerFactory.getLogger(StompServerVerticle.class);

    @Autowired
    private ContinuumGatewayProperties gatewayProperties;

    @Autowired
    private StompServerHandlerFactory stompServerHandlerFactory;

    private HttpServer httpServer;

    @Override
    public void start() {

        Router router = Router.router(vertx);
        router.route().handler(StaticHandler.create());

        // FIXME: check CORS, see if it is protected or actually allowing any..?
        StompServerOptions properties = gatewayProperties.getStomp();

        httpServer = vertx.createHttpServer(new HttpServerOptions().setWebSocketSubProtocols(List.of("v12.stomp")))
                          .webSocketHandler(StompServer.createWebSocketHandler(vertx,
                                                                               gatewayProperties.getStomp(),
                                                                               stompServerHandlerFactory))
                          .requestHandler(router)
                          .exceptionHandler(event -> log.error(
                                  "Stomp server Exception before completing Client Connection",
                                  event))
                          .listen(properties.getPort(), properties.getHost(), ar -> {
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

