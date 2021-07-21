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

import com.kinotic.continuum.core.api.CRI;
import com.kinotic.continuum.core.api.RpcServiceProxyHandle;
import com.kinotic.continuum.core.api.ServiceRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AbstractFactoryBean;


/**
 * Creates a {@link RpcServiceProxyHandle} for the given serviceInterfaceClassName when needed by spring.
 * Automatically destroys the {@link RpcServiceProxyHandle} when the context goes out of scope.
 *
 *
 * Created by Navid Mitchell on 04/17/19.
 */
public class RpcServiceProxyBeanFactory extends AbstractFactoryBean<Object> {

    private final String serviceInterfaceClassName;
    private final CRI cri;

    private Class<?> serviceClass;
    private RpcServiceProxyHandle<?> serviceHandle;

    @Autowired
    private ServiceRegistry serviceRegistry;


    public RpcServiceProxyBeanFactory(String serviceInterfaceClassName) {
        this.serviceInterfaceClassName = serviceInterfaceClassName;
        this.cri = null;
        setSingleton(true);
    }

    public RpcServiceProxyBeanFactory(String serviceInterfaceClassName,
                                      CRI cri) {
        this.serviceInterfaceClassName = serviceInterfaceClassName;
        this.cri = cri;
        setSingleton(true);
    }

    @Override
    protected synchronized void destroyInstance(Object instance) throws Exception {
        if(serviceHandle != null){
            serviceHandle.release();
        }
    }

    @Override
    public synchronized Class<?> getObjectType() {
        try {
            if(serviceClass == null){
                serviceClass = Class.forName(serviceInterfaceClassName);
            }
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(e);
        }
        return serviceClass;
    }

    @Override
    protected synchronized Object createInstance() {
        if(serviceHandle == null){
            if(cri == null) {
                serviceHandle = serviceRegistry.serviceProxy(getObjectType());
            }else{
                serviceHandle = serviceRegistry.serviceProxy(cri, getObjectType());
            }
        }
        return serviceHandle.getService();
    }
}
