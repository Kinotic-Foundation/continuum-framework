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

import { EventBus } from '@/core/api/EventBus'
import {IEventBus} from '@/core/api/IEventBus'
import { ServiceRegistry } from '@/core/api/ServiceRegistry'
import { IServiceProxy } from '@/core/api/IServiceRegistry'
import {Identifiable} from '@/api/Identifiable'
import {ICrudServiceProxy} from '@/core/api/crud/ICrudServiceProxy'
import {CrudServiceProxyFactory} from '@/core/api/crud/CrudServiceProxyFactory'
import {ConnectedInfo} from '@/api/security/ConnectedInfo'
import {ConnectionInfo} from '@/api/Connection'


/**
 * Provides a simplified way to connect to Continuum and access services.
 * All methods use a single connection to the Continuum Services
 */
export class ContinuumSingleton {
    public readonly eventBus!: IEventBus
    public readonly serviceRegistry!: ServiceRegistry
    public readonly crudServiceProxyFactory!: CrudServiceProxyFactory


    constructor() {
        this.eventBus = new EventBus()
        this.serviceRegistry = new ServiceRegistry(this.eventBus)
        this.crudServiceProxyFactory = new CrudServiceProxyFactory(this.serviceRegistry)
    }

    /**
     * Requests a connection to the given Stomp url
     * @param connectionInfo provides the information needed to connect to the continuum server
     * @return Promise containing the result of the initial connection attempt
     */
     public connect(connectionInfo: ConnectionInfo): Promise<ConnectedInfo> {
        return this.eventBus.connect(connectionInfo)
    }

    /**
     * Disconnects the client from the server
     * This will clear any subscriptions and close the connection
     */
    public disconnect(force?: boolean): Promise<void> {
        return this.eventBus.disconnect(force)
    }

    /**
     * Creates a new service proxy that can be used to access the desired service.
     * @param serviceIdentifier the identifier of the service to be accessed
     * @return the {@link IServiceProxy} that can be used to access the service
     */
    public serviceProxy(serviceIdentifier: string): IServiceProxy {
        return this.serviceRegistry.serviceProxy(serviceIdentifier)
    }

    public crudServiceProxy<T extends Identifiable<string>>(serviceIdentifier: string): ICrudServiceProxy<T> {
        return this.crudServiceProxyFactory.crudServiceProxy<T>(serviceIdentifier)
    }
}

export const Continuum = new ContinuumSingleton()
