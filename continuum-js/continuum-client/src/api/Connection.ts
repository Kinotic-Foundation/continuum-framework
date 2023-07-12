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
    host!: string
    port?: number | null
    useSSL?: boolean | null
    connectHeaders?: ConnectHeaders
}
