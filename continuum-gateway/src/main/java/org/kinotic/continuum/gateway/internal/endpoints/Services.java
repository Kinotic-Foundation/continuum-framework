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

import com.fasterxml.jackson.databind.ObjectMapper;
import io.opentelemetry.api.OpenTelemetry;
import io.vertx.core.Vertx;
import org.kinotic.continuum.api.Continuum;
import org.kinotic.continuum.api.config.ContinuumProperties;
import org.kinotic.continuum.core.api.event.EventBusService;
import org.kinotic.continuum.core.api.event.EventStreamService;
import org.kinotic.continuum.api.security.SecurityService;
import org.kinotic.continuum.core.api.security.SessionManager;
import org.kinotic.continuum.gateway.api.config.ContinuumGatewayProperties;
import org.kinotic.continuum.gateway.internal.endpoints.stomp.DefaultStompServerHandler;
import org.kinotic.continuum.internal.core.api.service.invoker.ExceptionConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Facade class to make it easier to get needed services into {@link DefaultStompServerHandler}
 * To keep the constructor args small and adding new service dependencies can just be done here...
 * Created by navid on 1/23/20
 */
@Component
public class Services {
    @Autowired
    public Continuum continuum;
    @Autowired
    public ContinuumGatewayProperties continuumGatewayProperties;
    @Autowired
    public ContinuumProperties continuumProperties;
    @Autowired
    public EventBusService eventBusService;
    @Autowired
    public EventStreamService eventStreamService;
    @Autowired
    public ExceptionConverter exceptionConverter;
    @Autowired
    public ObjectMapper objectMapper;
    @Autowired
    public OpenTelemetry openTelemetry;
    @Autowired
    public SecurityService securityService;
    @Autowired
    public SessionManager sessionManager;
    @Autowired
    public Vertx vertx;
}
