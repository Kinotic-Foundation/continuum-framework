import { IServiceProxy, IServiceRegistry, IEventFactory } from './IServiceRegistry';
import { IEvent, IEventBus } from './IEventBus';
export declare class JsonEventFactory implements IEventFactory {
    create(cri: string, args: any[] | null | undefined): IEvent;
}
export declare class TextEventFactory implements IEventFactory {
    create(cri: string, args: any[] | null | undefined): IEvent;
}
export declare class ServiceRegistry implements IServiceRegistry {
    private readonly eventBus;
    constructor(eventBus: IEventBus);
    serviceProxy(serviceIdentifier: string): IServiceProxy;
}
