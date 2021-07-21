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

package com.kinotic.continuum.gateway.api.config;

import com.kinotic.continuum.api.config.ContinuumProperties;
import io.vertx.ext.stomp.StompServerOptions;
import io.vertx.mqtt.MqttServerOptions;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created by Navid Mitchell on 7/19/17.
 */
@Component
@ConfigurationProperties(prefix = "continuum-gateway")
public class ContinuumGatewayProperties {
    /**
     * Default properties unless overridden in application properties
     */
    public static String    DEFAULT_DATA_DIRECTORY = "gateway-data/";
    public static int       DEFAULT_STOMP_PORT = 58503;
    public static String    DEFAULT_STOMP_WEBSOCKET_PATH = "/v1";
    public static int       DEFAULT_REST_PORT = 58504;
    public static String    DEFAULT_REST_PATH = "/api";
    public static long      DEFAULT_REST_BODY_LIMIT_SIZE = 2048;

    private String dataDir = DEFAULT_DATA_DIRECTORY;

    private final StompServerOptions stomp;

    private final MqttServerOptions mqtt;

    private final ContinuumRestServerProperties rest = new ContinuumRestServerProperties();

    private boolean disableIam;

    public ContinuumGatewayProperties(ContinuumProperties continuumProperties) {
        stomp = new StompServerOptions()
                        .setPort(DEFAULT_STOMP_PORT)
                        .setWebsocketPath(DEFAULT_STOMP_WEBSOCKET_PATH)
                        .setDebugEnabled(continuumProperties.isDebug());

        mqtt = new MqttServerOptions();
    }

    public String getDataDir() {
        return dataDir;
    }

    public void setDataDir(String dataDir) {
        this.dataDir = dataDir;
    }

    public ContinuumRestServerProperties getRest() {
        return rest;
    }

    public StompServerOptions getStomp() {
        return stomp;
    }

    public MqttServerOptions getMqtt() {
        return mqtt;
    }

    /**
     * Determines if the Iam is disabled or not
     * If it is disabled no com.kinotic.continuum.core.api.security.SecurityService will be required by the continuum cluster
     * @return true if the Iam is disabled false if not
     */
    public boolean isDisableIam() {
        return disableIam;
    }

    /**
     * Set if the IAM should be disabled or not
     * If it is disabled no com.kinotic.continuum.core.api.security.SecurityService will be required by the continuum cluster
     * @param disableIam true if the IAM should be disabled false if not
     */
    public void setDisableIam(boolean disableIam) {
        this.disableIam = disableIam;
    }
}
