import { EventBus } from '@/core/api/EventBus'
import {ConnectHeaders, IEventBus} from '@/core/api/IEventBus'
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
     * @param identity to use during connection
     * @param secret to use during connection
     * @return Promise containing the result of the initial connection attempt
     */
    export function connect(url: string, identity: string, secret: string): Promise<ConnectedInfo> {
        return eventBus.connect(url, identity, secret)
    }

    /**
     * Requests a connection to the given Stomp url.
     * This method allows for more advanced connection options.
     * All headers will be sent as part of the STOMP CONNECT frame.
     * @param url to connect to
     * @param connectHeaders to use during connection
     */
    export function connectAdvanced(url: string, connectHeaders: ConnectHeaders): Promise<ConnectedInfo>{
        return eventBus.connectAdvanced(url, connectHeaders)
    }

    /**
     * Disconnects the client from the server
     * This will clear any subscriptions and close the connection
     */
    export function disconnect(force?: boolean): Promise<void> {
        return eventBus.disconnect(force)
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
