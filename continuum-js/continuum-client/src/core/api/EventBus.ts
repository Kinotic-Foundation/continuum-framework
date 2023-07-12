/*
 *
 * Copyright 2008-2021 Kinotic and the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
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

import {IEvent, IEventBus, EventConstants, ConnectHeaders} from './IEventBus'
import { ConnectableObservable, Observable, Subject, Unsubscribable, Subscription, throwError } from 'rxjs'
import { filter, map, multicast } from 'rxjs/operators'
import { firstValueFrom } from 'rxjs'
import { RxStomp, IMessage, StompHeaders, IFrame } from '@stomp/rx-stomp'
import { Optional } from 'typescript-optional'
import { v4 as uuidv4 } from 'uuid'
import {ConnectedInfo} from '@/api/security/ConnectedInfo'
import {ContinuumError} from '@/api/errors/ContinuumError';

/**
 * Default IEvent implementation
 */
export class Event implements IEvent {

    public cri: string
    public headers: Map<string, string>
    public data: Optional<Uint8Array>

    constructor(cri: string,
                headers?: Map<string, string>,
                data?: Uint8Array) {

        this.cri = cri

        if (headers !== undefined) {
            this.headers = headers
        } else {
            this.headers = new Map<string, string>()
        }

        this.data = Optional.ofNullable(data)
    }


    public getHeader(key: string): string | undefined {
        return this.headers.get(key)
    }

    public hasHeader(key: string): boolean {
        return this.headers.has(key)
    }

    public setHeader(key: string, value: string): void {
        this.headers.set(key, value)
    }

    public removeHeader(key: string): boolean {
        return this.headers.delete(key)
    }

    public setDataString(data: string): void {
        const uint8Array = new TextEncoder().encode(data)
        this.data = Optional.ofNonNull(uint8Array)
    }

    public getDataString(): string {
        let ret = ''
        this.data.ifPresent(( value ) => ret = new TextDecoder().decode(value))
        return ret
    }
}

/**
 * Default implementation of {@link IEventBus}
 */
export class EventBus implements IEventBus {

    private stompClient: RxStomp | null = null
    private replyToCri: string  | null = null
    private requestRepliesObservable: ConnectableObservable<IEvent> | null = null
    private requestRepliesSubscription: Subscription | null = null
    private errorSubject: Subject<IFrame> = new Subject<IFrame>()
    private errorSubjectSubscription: Subscription | null | undefined = null

    public fatalErrors: Observable<Error>


    constructor() {
        this.fatalErrors = this.errorSubject.pipe(map<IFrame, Error>((frame: IFrame): Error => {
            this.disconnect()
                .catch((error: string) => {
                    if(console){
                        console.error('Error disconnecting from Stomp: ' + error)
                    }
                })
            // TODO: map to continuum error
            return new ContinuumError(frame.headers['message'])
        }))
    }

    public isConnectionActive(): boolean{
        return this.stompClient != null && this.stompClient.active
    }

    public connect(url: string, identity: string, secret: string): Promise<ConnectedInfo> {
        // TODO: change names to use identity and secret, it is more clear
        return this.connectAdvanced(url, {
            login: identity,
            passcode: secret
        })
    }

    connectAdvanced(url: string, connectHeaders: ConnectHeaders): Promise<ConnectedInfo> {
        return new Promise((resolve, reject): void => {
            if (!this.isConnectionActive()) {

                this.stompClient = new RxStomp()

                let connectHeadersInternal: StompHeaders = connectHeaders

                this.stompClient.configure({
                    brokerURL: url,
                    connectHeaders: connectHeadersInternal,
                    heartbeatIncoming: 120000,
                    heartbeatOutgoing: 30000,
                    reconnectDelay: 30000
                })

                // This subscription is to handle any errors that occur during connection
                const errorSubscription: Subscription = this.stompClient.stompErrors$.subscribe((value: IFrame)  => {
                    errorSubscription.unsubscribe()
                    const message = value.headers['message']
                    this.stompClient?.deactivate()
                    this.stompClient = null
                    reject(message)
                })

                const connectedSubscription: Subscription = this.stompClient.serverHeaders$.subscribe((value: StompHeaders) => {
                    connectedSubscription.unsubscribe()
                    let connectedInfoJson: string | undefined = value[EventConstants.CONNECTED_INFO_HEADER]
                    if (connectedInfoJson != null) {

                        const connectedInfo: ConnectedInfo = JSON.parse(connectedInfoJson)

                        if(connectedInfo.sessionId != null && connectedInfo.replyToId != null) {

                            // Remove all information originally sent from the connect headers
                            for (let key in connectHeaders) {
                                delete connectHeadersInternal[key]
                            }

                            connectHeadersInternal.session = connectedInfo.sessionId

                            this.replyToCri = EventConstants.SERVICE_DESTINATION_PREFIX + connectedInfo.replyToId + ':' + uuidv4() + '@continuum.js.EventBus/replyHandler'

                            resolve(connectedInfo)
                        }else {
                            reject('Server did not return proper data for successful login')
                        }
                    }else {
                        reject('Server did not return proper data for successful login')
                    }

                    // Connect the error subject for users to use
                    this.errorSubjectSubscription = this.stompClient?.stompErrors$.subscribe(this.errorSubject)

                })

                this.stompClient.activate()

            } else {
                reject('Stomp connection already active')
            }
        })
    }

    public async disconnect(force?: boolean): Promise<void> {
        if (this.isConnectionActive()) {
            if (this.requestRepliesObservable != null) {
                if (this.requestRepliesSubscription != null) {
                    this.requestRepliesSubscription.unsubscribe()
                    this.requestRepliesSubscription = null
                }
                this.requestRepliesObservable = null
            }

            if (this.errorSubjectSubscription) {
                this.errorSubjectSubscription.unsubscribe()
                this.errorSubjectSubscription = null
            }

            await this.stompClient?.deactivate({force: force})

            this.stompClient = null
        }
        return
    }

    public send(event: IEvent): void {
        if(this.isConnectionActive()){
            const headers: any = {}

            for (const [key, value] of event.headers.entries()) {
                headers[key] = value
            }

            // send data over stomp
            // @ts-ignore
            this.stompClient.publish({
                destination: event.cri,
                headers,
                binaryBody: event.data.orUndefined()
            })
        }else{
            throw new Error('You must call connect on the event bus before sending any event')
        }
    }

    public request(event: IEvent): Promise<IEvent> {
        return firstValueFrom(this.requestStream(event, false))
    }

    public requestStream(event: IEvent, sendControlEvents: boolean = true): Observable<IEvent> {
        if(this.isConnectionActive()){
            return new Observable<IEvent>((subscriber) => {

                if (this.requestRepliesObservable == null) {
                    this.requestRepliesObservable = this._observe(this.replyToCri as string).pipe(multicast(new Subject())) as ConnectableObservable<IEvent>
                    this.requestRepliesSubscription = this.requestRepliesObservable.connect()
                }

                let serverSignaledCompletion = false
                const correlationId = uuidv4()
                const defaultMessagesSubscription: Unsubscribable = this.requestRepliesObservable
                    .pipe(filter((value: IEvent): boolean => {
                        return value.headers.get(EventConstants.CORRELATION_ID_HEADER) === correlationId
                    })).subscribe({
                        next(value: IEvent): void {

                            if (value.hasHeader(EventConstants.CONTROL_HEADER)) {

                                if (value.headers.get(EventConstants.CONTROL_HEADER) === 'complete') {
                                    serverSignaledCompletion = true
                                    subscriber.complete()
                                } else {
                                    throw new Error('Control Header ' + value.headers.get(EventConstants.CONTROL_HEADER) + ' is not supported')
                                }

                            } else if (value.hasHeader(EventConstants.ERROR_HEADER)) {

                                // TODO: add custom error type that contains error detail as well if provided by server, this would be the event body
                                serverSignaledCompletion = true
                                subscriber.error(new Error(value.getHeader(EventConstants.ERROR_HEADER)))

                            } else {

                                subscriber.next(value)

                            }
                        },
                        error(err: any): void {
                            subscriber.error(err)
                        },
                        complete(): void {
                            subscriber.complete()
                        }
                    })

                subscriber.add(defaultMessagesSubscription)

                event.setHeader(EventConstants.REPLY_TO_HEADER, this.replyToCri as string)
                event.setHeader(EventConstants.CORRELATION_ID_HEADER, correlationId)

                this.send(event)

                return () => {
                    if (sendControlEvents && !serverSignaledCompletion) {
                        // create control event to cancel long-running request
                        const controlEvent: Event = new Event(event.cri)
                        controlEvent.setHeader(EventConstants.CONTROL_HEADER, EventConstants.CONTROL_VALUE_CANCEL)
                        controlEvent.setHeader(EventConstants.CORRELATION_ID_HEADER, correlationId)
                        this.send(controlEvent)
                    }
                }
            })
        }else{
            return throwError(() => new Error('You must call connect on the event bus before sending any request'))
        }

    }

    public observe(cri: string): Observable<IEvent> {
       return this._observe(cri)
    }

    /**
     * This is internal impl of observe that creates a cold observable.
     * The public variants transform this to some type of hot observable depending on the need
     * @param cri to observe
     * @return the cold {@link Observable<IEvent>} for the given destination
     */
    private _observe(cri: string): Observable<IEvent> {
        if(this.isConnectionActive()) {
            // @ts-ignore
            return this.stompClient
                       .watch(cri)
                       .pipe(map<IMessage, IEvent>((message: IMessage): IEvent => {

                           // We translate all IMessage objects to IEvent objects
                           const headers: Map<string, string> = new Map<string, string>()

                           for (const prop of Object.keys(message.headers)) {
                               headers.set(prop, message.headers[prop])
                           }

                           return new Event(cri, headers, message.binaryBody)
                       }))
        }else{
            return throwError(() => new Error('You must call connect on the event bus before sending any request'))
        }
    }

}

