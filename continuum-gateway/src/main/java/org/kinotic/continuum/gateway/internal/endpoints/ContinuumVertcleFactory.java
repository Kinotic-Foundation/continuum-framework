package org.kinotic.continuum.gateway.internal.endpoints;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.ext.stomp.lite.StompServerHandlerFactory;
import org.kinotic.continuum.api.config.ContinuumProperties;
import org.kinotic.continuum.api.security.SecurityService;
import org.kinotic.continuum.core.api.event.EventBusService;
import org.kinotic.continuum.gateway.api.config.ContinuumGatewayProperties;
import org.kinotic.continuum.gateway.internal.endpoints.rest.RestServerVerticle;
import org.kinotic.continuum.gateway.internal.endpoints.stomp.StompServerVerticle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Provides a factory for creating continuum end point verticles.
 * Created by Navíd Mitchell 🤪 on 3/6/24.
 */
@Component
public class ContinuumVertcleFactory {

    private final ContinuumProperties continuumProperties;
    private final ContinuumGatewayProperties gatewayProperties;
    private final StompServerHandlerFactory stompServerHandlerFactory;
    private final EventBusService eventService;
    private final SecurityService securityService;
    private final ObjectMapper objectMapper;

    public ContinuumVertcleFactory(ContinuumProperties continuumProperties,
                                   ContinuumGatewayProperties gatewayProperties,
                                   StompServerHandlerFactory stompServerHandlerFactory,
                                   EventBusService eventService,
                                   ObjectMapper objectMapper,
                                   @Autowired(required = false) SecurityService securityService) {
        this.continuumProperties = continuumProperties;
        this.gatewayProperties = gatewayProperties;
        this.stompServerHandlerFactory = stompServerHandlerFactory;
        this.eventService = eventService;
        this.objectMapper = objectMapper;
        this.securityService = securityService;
    }

    public StompServerVerticle createStompServerVerticle(){
        return new StompServerVerticle(continuumProperties, gatewayProperties, stompServerHandlerFactory);
    }

    public RestServerVerticle createRestServerVerticle(){
       return new RestServerVerticle(gatewayProperties, eventService, securityService, objectMapper);
    }

}
