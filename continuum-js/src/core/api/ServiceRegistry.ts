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

import { IServiceProxy, IServiceRegistry, IEventFactory } from './IServiceRegistry'
import { EventConstants, IEvent, IEventBus } from './IEventBus'
import { Event } from './EventBus'
import { Observable } from 'rxjs'
import { first, map } from 'rxjs/operators'
import { injectable, inject, container } from 'inversify-props'


export class JsonEventFactory implements IEventFactory {

    create(cri: string, args: any[] | null | undefined): IEvent {
        const event: Event = new Event(cri)

        event.setHeader(EventConstants.CONTENT_TYPE_HEADER, EventConstants.CONTENT_JSON)

        if(args != null) {
            event.setDataString(JSON.stringify(args))
        }

        return event
    }

}

export class TextEventFactory implements IEventFactory {

    create(cri: string, args: any[] | null | undefined): IEvent {
        const event: Event = new Event(cri)

        event.setHeader(EventConstants.CONTENT_TYPE_HEADER, EventConstants.CONTENT_TEXT)
        if (args != null) {
            let data: string = ''
            let i = 0

            for (const arg of args) {
                if (i > 0) {
                    data = data + '\n'
                }
                data = data + arg
                i++
            }

            if (data.length > 0) {
                event.setDataString(data)
            }
        }
        return event
    }

}

@injectable()
export class ServiceRegistry implements IServiceRegistry {

    private readonly eventBus: IEventBus

    constructor(@inject() eventBus: IEventBus) {
         this.eventBus = eventBus
    }

    public serviceProxy(serviceIdentifier: string): IServiceProxy {
        return new ServiceProxy(serviceIdentifier, this.eventBus)
    }
}

const defaultEventFactory: IEventFactory = new JsonEventFactory()

/**
 * For internal use only should not be instantiated directly
 */
class ServiceProxy implements IServiceProxy {

    public readonly serviceIdentifier: string
    private readonly eventBus: IEventBus


    constructor(serviceIdentifier: string, eventBus: IEventBus) {
        if ( typeof serviceIdentifier === 'undefined' || serviceIdentifier.length === 0 ) {
            throw new Error('The serviceIdentifier provided must contain a value')
        }
        this.serviceIdentifier = serviceIdentifier
        this.eventBus = eventBus
    }


    invoke(methodIdentifier: string,
           args?: any[] | null | undefined,
           scope?: string | null | undefined,
           eventFactory?: IEventFactory | null | undefined): Promise<any> {
        return this.__invokeStream(false, methodIdentifier, args, scope, eventFactory).pipe(first()).toPromise()
    }

    invokeStream(methodIdentifier: string,
                 args?: any[] | null | undefined,
                 scope?: string | null | undefined,
                 eventFactory?: IEventFactory | null | undefined): Observable<any> {
        return this.__invokeStream(true, methodIdentifier, args, scope, eventFactory)
    }

    private __invokeStream(sendControlEvents: boolean,
                           methodIdentifier: string,
                           args?: any[] | null | undefined,
                           scope?: string | null | undefined,
                           eventFactory?: IEventFactory | null | undefined): Observable<any> {

        const cri: string = EventConstants.SERVICE_DESTINATION_PREFIX + (scope != null ? scope + '@' : '') + this.serviceIdentifier + '/' + methodIdentifier
        const eventFactoryToUse = (eventFactory != null ? eventFactory : defaultEventFactory)
        let event: IEvent = eventFactoryToUse.create(cri, args)

        return this.eventBus.requestStream(event, sendControlEvents)
            .pipe(map<IEvent, any>((value: IEvent): any => {

                const contentType: string | undefined = value.getHeader(EventConstants.CONTENT_TYPE_HEADER)

                if (contentType !== undefined) {
                    if (contentType === 'application/json') {
                        return JSON.parse(value.getDataString())
                    } else if (contentType === 'text/plain') {
                        return value.getDataString()
                    } else {
                        throw new Error('Content Type ' + contentType + ' is unknown')
                    }
                } else {
                    return null
                }
        }))
    }
}

container.addSingleton<IServiceRegistry>(ServiceRegistry)
