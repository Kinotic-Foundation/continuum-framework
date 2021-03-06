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

package com.kinotic.continuum.internal;

import com.kinotic.continuum.api.annotations.Publish;
import com.kinotic.continuum.core.api.RpcServiceProxy;
import com.kinotic.continuum.core.api.ServiceRegistry;
import com.kinotic.continuum.core.api.service.ServiceIdentifier;
import com.kinotic.continuum.internal.utils.MetaUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.DestructionAwareBeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.BiConsumer;

/**
 * Registers configured beans to participate in Continuum functionality
 *
 *
 * Created by Navid Mitchell on 11/28/18.
 */
@Component
public class ServiceRegistrationBeanPostProcessor implements DestructionAwareBeanPostProcessor {

    private static final Logger log = LoggerFactory.getLogger(ServiceRegistrationBeanPostProcessor.class);

    private final ServiceRegistry serviceRegistry;

    public ServiceRegistrationBeanPostProcessor(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }

    @Override
    public void postProcessBeforeDestruction(Object bean, String beanName) throws BeansException {
        processBean(bean, (serviceIdentifier, clazz) -> {

            log.info("Un-Registering Service "+ serviceIdentifier);

            serviceRegistry.unregister(serviceIdentifier)
                           .subscribe(null,
                                      throwable -> log.error("Error Un-Registering service "+ serviceIdentifier, throwable));
        });
    }
    
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

        processBean(bean, (serviceIdentifier, clazz) -> {

            log.info("Registering Service "+ serviceIdentifier);

            serviceRegistry.register(serviceIdentifier, clazz, bean)
                           .subscribe(null,
                                      throwable -> log.error("Error Registering service "+ serviceIdentifier, throwable));
        });
        return bean;
    }

    private void processBean(Object instance, BiConsumer<ServiceIdentifier, Class<?>> consumer){
        // Do not wrap RpcServiceProxies with invokers.. Infinite Recursion boom!
        if(!(instance instanceof RpcServiceProxy)) {
            try {
                // See if any of the interfaces have a publish annotation
                Class<?> clazz = instance.getClass();
                List<Class<?>> interfaces = MetaUtil.getInterfaceDeclaringAnnotation(clazz, Publish.class);

                if (!interfaces.isEmpty()) {

                    for (Class<?> inter : interfaces) {

                        Publish publish = AnnotationUtils.findAnnotation(inter, Publish.class);
                        String namespace = publish.namespace().isEmpty() ? encodePackageName(inter.getPackageName()) : publish.namespace();
                        String name = publish.name().isEmpty() ? inter.getSimpleName() : publish.name();
                        String scope = MetaUtil.getScopeIfAvailable(instance, inter);
                        ServiceIdentifier serviceIdentifier = new ServiceIdentifier(namespace, name, scope, publish.version());

                        consumer.accept(serviceIdentifier, inter);
                    }
                }
            } catch (Exception e) {
                log.warn("Error processing Meta for bean:" + instance, e);
            }
        }
    }

    /**
     * Encodes characters allowed in package name but not in an URI
     * @param packageName of the package to encode
     * @return the encoded package name
     */
    private String encodePackageName(String packageName){
        return packageName.replaceAll("_", "-").replaceAll("\\$", "-");
    }

}
