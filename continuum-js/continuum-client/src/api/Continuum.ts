import { EventBus } from '@/core/api/EventBus'
import { IEventBus } from '@/core/api/IEventBus'
import { ServiceRegistry } from '@/core/api/ServiceRegistry'
import { IServiceProxy } from '@/core/api/IServiceRegistry'
import {Identifiable} from '@/api/Identifiable'
import {ICrudServiceProxy} from '@/core/api/crud/ICrudServiceProxy'
import {CrudServiceProxyFactory} from '@/core/api/crud/CrudServiceProxyFactory'
import {ConnectedInfo} from '@/api/security/ConnectedInfo'

/**
 * Provides a simplified way to connect to Continuum and access services
 */
export namespace Continuum {

    export const eventBus: IEventBus = new EventBus()

    const serviceRegistry = new ServiceRegistry(eventBus)

    const crudServiceProxyFactory = new CrudServiceProxyFactory(serviceRegistry)

    /**
     * Requests a connection to the given Stomp url
     * @param url to connect to
     * @param accessKey to use during connection
     * @param secretToken to use during connection
     * @return Promise containing the result of the initial connection attempt
     */
    export function connect(url: string, accessKey: string, secretToken: string): Promise<ConnectedInfo> {
        return eventBus.connect(url, accessKey, secretToken)
    }

    /**
     * Disconnects the client from the server
     * This will clear any subscriptions and close the connection
     */
    export function disconnect(): Promise<void> {
        return eventBus.disconnect()
    }

    /**
     * Creates a new service proxy that can be used to access the desired service.
     * @param serviceIdentifier the identifier of the service to be accessed
     * @return the {@link IServiceProxy} that can be used to access the service
     */
    export function serviceProxy(serviceIdentifier: string): IServiceProxy {
        return serviceRegistry.serviceProxy(serviceIdentifier)
    }

    export function crudServiceProxy<T extends Identifiable<string>>(serviceIdentifier: string): ICrudServiceProxy<T> {
        return crudServiceProxyFactory.crudServiceProxy<T>(serviceIdentifier)
    }

}
