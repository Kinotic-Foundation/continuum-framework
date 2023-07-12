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

package org.kinotic.continuum.gateway.internal.config;

import org.kinotic.continuum.api.security.SecurityService;
import org.kinotic.continuum.core.api.service.ServiceIdentifier;
import org.kinotic.continuum.internal.RpcServiceProxyBeanFactory;
import org.kinotic.continuum.gateway.internal.api.security.DummySecurityService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Setups correct {@link SecurityService} implementation for production use.
 *
 *
 * Created by navid on 2/10/20
 */
@Configuration
public class ContinuumGatewaySecurityConfig {

    @Bean
    @ConditionalOnProperty(
            value="continuum-gateway.disableIam",
            havingValue = "false",
            matchIfMissing = true)
    @ConditionalOnMissingBean(SecurityService.class)
    RpcServiceProxyBeanFactory securityServiceFactory(){
        return new RpcServiceProxyBeanFactory(SecurityService.class,
                                              new ServiceIdentifier("org.kinotic.continuum.core.api.security",
                                                                    "SecurityService",
                                                                    null,
                                                                    "0.1.0"));
    }

    @Bean
    @ConditionalOnProperty(
            value="continuum-gateway.disableIam",
            havingValue = "true")
    @ConditionalOnMissingBean
    SecurityService dummySecurityService(){
        return new DummySecurityService();
    }

}
