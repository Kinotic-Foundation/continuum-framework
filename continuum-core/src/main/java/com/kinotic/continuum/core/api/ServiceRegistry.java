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

package com.kinotic.continuum.core.api;

import reactor.core.publisher.Mono;

/**
 * Provides the functionality to register services as well as access proxies for those services
 *
 *
 * Created by Navid Mitchell on 2019-02-08.
 */
public interface ServiceRegistry {

    /**
     * Registers a new service with the service registry.
     * This will allow the service to be accessed remotely.
     *
     * @param cri the identifier that must be used to access this service remotely
     * @param serviceInterface the Interface that this service implements
     * @param instance the instance of this service that will respond to service invocations
     * @return {@link Mono} that will be completed on success or failed on an error
     *         NOTE: {@link Mono} will contain IllegalArgumentException if the service identifier is already registered with the registry
     */
    Mono<Void> register(CRI cri, Class<?> serviceInterface, Object instance);

    /**
     * Remove a service from the service registry.
     *
     * @param cri the identifier that must be used to identify the service
     * @return a {@link Mono} that will be completed when the service is unregistered
     */
    Mono<Void> unregister(CRI cri);

    /**
     * Creates a new service proxy that can be used to access the desired service.
     * @param <T> the type of the serviceInterface
     * @param cri the identifier of the service to be accessed
     * @param serviceInterface the interface that the service should implement and the target class of the returned proxy
     * @return the {@link RpcServiceProxyHandle <T>} that can be used to access the service
     */
    <T> RpcServiceProxyHandle<T> serviceProxy(CRI cri, Class<T> serviceInterface);

    /**
     * Creates a new service proxy that can be used to access the desired service.
     * @param <T> the type of the serviceInterface
     * @param cri the identifier of the service to be accessed
     * @param serviceInterface the interface that the service should implement and the target class of the returned proxy
     * @param contentTypeExpected a String containing the content type expected by the receiver of the RPC request
     * @return the {@link RpcServiceProxyHandle <T>} that can be used to access the service
     */
    <T> RpcServiceProxyHandle<T> serviceProxy(CRI cri, Class<T> serviceInterface, String contentTypeExpected);

    /**
     * Creates a new service proxy that can be used to access the desired service.
     * NOTE: the interface provided must have a {@link com.kinotic.continuum.api.annotations.Proxy} annotation
     * @param serviceInterface the interface that the service should implement and the target class of the returned proxy
     * @param <T> the type of the serviceInterface
     * @return the {@link RpcServiceProxyHandle <T>} that can be used to access the service
     * @throws IllegalArgumentException if the {@link com.kinotic.continuum.api.annotations.Proxy} annotation is not present or the proxy value is not provided
     */
    <T> RpcServiceProxyHandle<T> serviceProxy(Class<T> serviceInterface);
}
