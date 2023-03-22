import { EventBus } from "@/core/api/EventBus"
import { ServiceRegistry } from "@/core/api/ServiceRegistry"
import { IServiceProxy } from "@/core/api/IServiceRegistry"

export namespace Continuum {

    const eventBus = new EventBus()

    const serviceRegistry = new ServiceRegistry(eventBus)

    export function connect(url: string, identity: string, secret: string): Promise<void> {
        return eventBus.connect(url, identity, secret)
    }

    export function disconnect(): Promise<void> {
        return eventBus.disconnect()
    }

    export function serviceProxy(serviceIdentifier: string): IServiceProxy {
        return serviceRegistry.serviceProxy(serviceIdentifier)
    }

}
