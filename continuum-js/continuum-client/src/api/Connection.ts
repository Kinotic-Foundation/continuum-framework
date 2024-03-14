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

/**
 * ConnectHeaders to use during connection to the continuum server
 * These headers will be sent as part of the STOMP CONNECT frame
 * This is typically used for authentication information, but any data can be sent
 */
export class ConnectHeaders {
    [key: string]: string
}

/**
 * ConnectionInfo provides the information needed to connect to the continuum server
 */
export class ConnectionInfo {
    connectHeaders?: ConnectHeaders
    /**
     * The amount of time in milliseconds to wait for the connection to be established, before completing with an error
     */
    connectionTimeout?: number | null
    /**
     * The number of times to retry the connection attempt after the connectionTimeout is reached.
     */
    connectionRetries?: number | null
    host!: string
    port?: number | null
    useSSL?: boolean | null
}
