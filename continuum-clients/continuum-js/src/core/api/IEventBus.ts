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

import { Optional } from 'typescript-optional'
import { Observable } from 'rxjs'

/**
 * Part of the low level portion of continuum representing data to be processed
 *
 * This is similar to a Stomp Frame but with more required information and no control plane semantics.
 *
 *
 * Created by Navid Mitchell on 2019-01-04.
 */
export interface IEvent {

    /**
     * The cri that specifies where the event should be routed
     */
    cri: string

    /**
     * Any headers defined for this event.
     * This will usually contain all the fields above as well since they are typically wrappers around expected header values.
     */
    headers: Map<string, string>

    /**
     * The event payload. The payload depends on the type the payload is encoded into a media format which is specified by the contentType attribute (e.g. application/json).
     */
    data: Optional<Uint8Array>

    /**
     * Gets the value for the header with the given key
     * @param key to get the header value for
     * @return the header value or undefined if there is no header for the key
     */
    getHeader(key: string): string | undefined

    /**
     * Tests if a header for the given key exists
     * @param key to check if exists as a header
     * @return true if the header for the key exists false if not
     */
    hasHeader(key: string): boolean

    /**
     * Removes the header from the headers map
     * @param key to remove
     * @return true if an element in the headers map object existed and has been removed, or false if the element does not exist
     */
    removeHeader(key: string): boolean

    /**
     * Sets the header into the headers map
     * @param key the key to use
     * @param value the value to use
     */
    setHeader(key: string, value: string): void

    /**
     * Sets the data property from the given string value
     * @param data
     */
    setDataString(data: string): void

    /**
     * @return the data property as an UTF-8 encoded string
     */
    getDataString(): string

}

/**
 * Part of the low level portion of continuum representing a connection to a continuum server
 * This is similar to a Stomp Client but with more required information and no control plane semantics.
 *
 * Created by Navid Mitchell on 2019-01-04.
 */
export interface IEventBus {

    /**
     * Requests a connection to the given Stomp url
     * @param url to connect to
     * @param accessKey to use during connection
     * @param secretToken to use during connection
     * @return Promise containing the result of the initial connection attempt
     */
    connect(url: string, accessKey: string, secretToken: string): Promise<void>

    /**
     * Disconnects the client from the server
     * This will clear any subscriptions and close the connection
     */
    disconnect(): void

    /**
     * Send a single {@link IEvent} to the connected server
     * @param event to send
     */
    send(event: IEvent): void

    /**
     * Sends an {@link IEvent} expecting a response
     * All response correlation will be handled internally
     * @param event to send as the request
     * @return a Promise that will resolve when the response is received
     */
    request(event: IEvent): Promise<IEvent>

    /**
     * Sends an {@link IEvent} expecting multiple responses
     * All response correlation will be handled internally
     * @param event to send as the request
     * @param sendControlEvents if true then control events will be sent to the server when changes to the returned to Observable are requested
     * @return an {@link Observable<IEvent} that will provide the response stream
     */
    requestStream(event: IEvent, sendControlEvents: boolean): Observable<IEvent>

    /**
     * Creates a subscription for all {@link IEvent}'s for the given destination
     * @param cri to subscribe to
     */
    observe(cri: string): Observable<IEvent>

}

/**
 * Constants used within {@link IEvent}'s to control the flow of events
 */
export enum EventConstants {
    CONTENT_TYPE_HEADER = 'content-type',
    CONTENT_LENGTH_HEADER = 'content-length',
    REPLY_TO_HEADER = 'reply-to',

    /**
     * Header provided by the sever on connection to represent the users session id
     */
    SESSION_HEADER = 'session',

    /**
     * Header provided by the sever on connection to represent the servers session key
     */
    SESSION_KEY_HEADER = 'sessionKey',

    /**
     * Correlates a response with a given request
     * Headers that start with __ will always be persisted between messages
     */
    CORRELATION_ID_HEADER = '__correlation-id',

    /**
     * Denotes that something caused an error. Will contain a brief message about the error
     */
    ERROR_HEADER = 'error',

    /**
     * Denotes the completion of an event stream. The value typically will contain the reason for completion.
     */
    COMPLETE_HEADER = 'complete',

    /**
     * Denotes the event is a control plane event. These are used for internal coordination.
     */
    CONTROL_HEADER = 'control',

    /**
     * Stream is complete no further values will be sent.
     */
    CONTROL_VALUE_COMPLETE = 'complete',

    CONTROL_VALUE_CANCEL = 'cancel',

    CONTROL_VALUE_SUSPEND = 'suspend',

    CONTROL_VALUE_RESUME = 'resume',

    SERVICE_DESTINATION_PREFIX = 'srv://',
    STREAM_DESTINATION_PREFIX =  'stream://',

    CONTENT_JSON = 'application/json',
    CONTENT_TEXT = 'text/plain'
}
