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

import com.kinotic.continuum.core.api.*;
import com.kinotic.continuum.api.annotations.Publish;
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
        processBean(bean, (cri, clazz) -> {

            log.debug("Un-Registering Service "+ cri);

            serviceRegistry.unregister(cri)
                           .subscribe(null,
                                      throwable -> log.error("Error Un-Registering service "+ cri, throwable));
        });
    }
    
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

        processBean(bean, (cri, clazz) -> {

            log.debug("Registering Service "+ cri);

            serviceRegistry.register(cri, clazz, bean)
                           .subscribe(null,
                                      throwable -> log.error("Error Registering service "+ cri, throwable));
        });
        return bean;
    }

    private void processBean(Object instance, BiConsumer<CRI, Class<?>> consumer){
        // Do not wrap RpcServiceProxies with invokers.. Infinite Recursion boom!
        if(!(instance instanceof RpcServiceProxy)) {
            try {
                // See if any of the interfaces have a publish annotation
                Class<?> clazz = instance.getClass();
                List<Class<?>> interfaces = MetaUtil.getInterfaceDeclaringAnnotation(clazz, Publish.class);

                if (!interfaces.isEmpty()) {

                    for (Class<?> inter : interfaces) {
                        Publish publish = AnnotationUtils.findAnnotation(inter, Publish.class);

                        CRI cri = CRI.create(Scheme.SERVICE,
                                             MetaUtil.getScopeIfAvailable(instance, inter),
                                             publish.value().isEmpty() ? inter.getName() : publish.value(),
                                             publish.version(),
                                             null);

                        consumer.accept(cri, inter);
                    }
                }
            } catch (Exception e) {
                log.warn("Error processing Meta for bean:" + instance, e);
            }
        }
    }

}
