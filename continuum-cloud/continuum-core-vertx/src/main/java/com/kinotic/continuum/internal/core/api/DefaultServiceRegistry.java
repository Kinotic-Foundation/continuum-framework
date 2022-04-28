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

package com.kinotic.continuum.internal.core.api;

import com.kinotic.continuum.api.Continuum;
import com.kinotic.continuum.api.annotations.Proxy;
import com.kinotic.continuum.core.api.RpcServiceProxyHandle;
import com.kinotic.continuum.core.api.ServiceRegistry;
import com.kinotic.continuum.core.api.event.EventBusService;
import com.kinotic.continuum.core.api.service.ServiceDescriptor;
import com.kinotic.continuum.core.api.service.ServiceFunctionInstanceProvider;
import com.kinotic.continuum.core.api.service.ServiceIdentifier;
import com.kinotic.continuum.internal.ServiceRegistrationBeanPostProcessor;
import com.kinotic.continuum.internal.core.api.service.invoker.ArgumentResolverComposite;
import com.kinotic.continuum.internal.core.api.service.invoker.ExceptionConverterComposite;
import com.kinotic.continuum.internal.core.api.service.invoker.ReturnValueConverterComposite;
import com.kinotic.continuum.internal.core.api.service.invoker.ServiceInvocationSupervisor;
import com.kinotic.continuum.internal.core.api.service.rpc.DefaultRpcServiceProxyHandle;
import com.kinotic.continuum.internal.core.api.service.rpc.RpcArgumentConverter;
import com.kinotic.continuum.internal.core.api.service.rpc.RpcArgumentConverterResolver;
import com.kinotic.continuum.internal.core.api.service.rpc.RpcReturnValueHandlerFactory;
import io.vertx.core.Vertx;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ReactiveAdapterRegistry;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeTypeUtils;
import reactor.core.publisher.Mono;

import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * Created by Navid Mitchell on 2019-03-20.
 */
@Component
public class DefaultServiceRegistry implements ServiceRegistry {

    private static final Logger log = LoggerFactory.getLogger(ServiceRegistrationBeanPostProcessor.class);

    @Autowired
    private Vertx vertx; //TODO: move thread scheduling and execution functionality into Continuum API such as Scheduling Service ect..

    @Autowired
    private EventBusService eventBusService;

    @Autowired
    private Continuum continuum;

    // These converters are used by ServiceInvocationSupervisor
    @Autowired
    private ArgumentResolverComposite argumentResolver;
    @Autowired
    private ReturnValueConverterComposite returnValueConverter;
    @Autowired
    private ExceptionConverterComposite exceptionConverter;

    // These are used for proxy side logic
    @Autowired
    private RpcArgumentConverterResolver rpcArgumentConverterResolver;

    @Autowired
    private RpcReturnValueHandlerFactory rpcReturnValueHandlerFactory;

    @Autowired
    private ReactiveAdapterRegistry reactiveAdapterRegistry;

    private ConcurrentHashMap<ServiceIdentifier, ServiceInvocationSupervisor> supervisors = new ConcurrentHashMap<>();


    @Override
    public Mono<Void> register(ServiceIdentifier serviceIdentifier, Class<?> serviceInterface, Object instance) {
        try {
            return register(ServiceDescriptor.create(serviceIdentifier, serviceInterface), ServiceFunctionInstanceProvider.create(instance));
        } catch (Exception e) {
            return Mono.error(e);
        }
    }

    @Override
    public Mono<Void> register(ServiceDescriptor serviceDescriptor, ServiceFunctionInstanceProvider instanceProvider) {
        return Mono.create(sink -> supervisors.compute(serviceDescriptor.serviceIdentifier(),
                                                       (serviceIdentifier, serviceInvocationSupervisor) -> {
                                                           if(serviceInvocationSupervisor == null){
                                                               try {
                                                                   serviceInvocationSupervisor = new ServiceInvocationSupervisor(
                                                                           serviceDescriptor,
                                                                           instanceProvider,
                                                                           argumentResolver,
                                                                           returnValueConverter,
                                                                           exceptionConverter,
                                                                           eventBusService,
                                                                           reactiveAdapterRegistry,
                                                                           vertx);

                                                                   serviceInvocationSupervisor
                                                                           .start()
                                                                           .subscribe(sink::success,
                                                                                      sink::error);

                                                               } catch (Exception e) {
                                                                   sink.error(e);
                                                               }
                                                           }else{
                                                               sink.error(new IllegalArgumentException("Service already registered for ServiceIdentifier "+ serviceDescriptor.serviceIdentifier()));
                                                           }
                                                           return serviceInvocationSupervisor;
                                                       }));
    }

    @Override
    public Mono<Void> unregister(ServiceIdentifier serviceIdentifier) {
        return Mono.create(sink -> supervisors.compute(serviceIdentifier,
                                                       (serviceIdentifier1, serviceInvocationSupervisor) -> {
                                                           if(serviceInvocationSupervisor != null){
                                                               serviceInvocationSupervisor
                                                                       .stop()
                                                                       .subscribe(sink::success,
                                                                                  sink::error);
                                                           }else{
                                                               sink.error(new IllegalArgumentException(" No Service registered for for ServiceIdentifier "+ serviceIdentifier));
                                                           }
                                                           return null; // remove from map
                                                       }));
    }

    @Override
    public <T> RpcServiceProxyHandle<T> serviceProxy(ServiceIdentifier serviceIdentifier, Class<T> serviceInterface) {
        RpcArgumentConverter rpcArgumentConverter = rpcArgumentConverterResolver.resolve(MimeTypeUtils.APPLICATION_JSON_VALUE);
        return new DefaultRpcServiceProxyHandle<>(serviceIdentifier,
                                                  continuum.nodeName(),
                                                  serviceInterface,
                                                  rpcArgumentConverter,
                                                  rpcReturnValueHandlerFactory,
                                                  eventBusService,
                                                  Thread.currentThread().getContextClassLoader());
    }

    @Override
    public <T> RpcServiceProxyHandle<T> serviceProxy(ServiceIdentifier serviceIdentifier, Class<T> serviceInterface, String contentTypeExpected) {
        Validate.notBlank(contentTypeExpected, "The contentTypeExpected must not be blank");
        Validate.isTrue(rpcArgumentConverterResolver.canResolve(contentTypeExpected), "The contentType:"+contentTypeExpected+" does not have any configured RpcArgumentConverter's");
        RpcArgumentConverter rpcArgumentConverter = rpcArgumentConverterResolver.resolve(contentTypeExpected);
        return new DefaultRpcServiceProxyHandle<>(serviceIdentifier,
                                                  continuum.nodeName(),
                                                  serviceInterface,
                                                  rpcArgumentConverter,
                                                  rpcReturnValueHandlerFactory,
                                                  eventBusService,
                                                  Thread.currentThread().getContextClassLoader());
    }

    @Override
    public <T> RpcServiceProxyHandle<T> serviceProxy(Class<T> serviceInterface) {
        Proxy proxyAnnotation = serviceInterface.getAnnotation(Proxy.class);
        Validate.notNull(proxyAnnotation, "The Class provided must be annotated with @Proxy");
        ServiceIdentifier serviceIdentifier = new ServiceIdentifier(!proxyAnnotation.namespace().isEmpty() ? proxyAnnotation.namespace() : null,
                                                                    proxyAnnotation.name(),
                                                                    null,
                                                                    proxyAnnotation.version());
        return serviceProxy(serviceIdentifier, serviceInterface, MimeTypeUtils.APPLICATION_JSON_VALUE);
    }
}
