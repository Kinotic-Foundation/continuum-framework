import {ConnectionInfo} from '@/api/Connection'
import {ConnectedInfo} from '@/api/security/ConnectedInfo'
import {EventConstants} from '@/core/api/IEventBus'
import {IFrame, RxStomp, StompHeaders} from '@stomp/rx-stomp'
import {Subscription} from 'rxjs'

/**
 * Creates a new RxStomp client and manages it
 * This is here to simplify the logic needed for connection management and the usage of the client.
 */
export class StompConnectionManager {

    private readonly INITIAL_RECONNECT_DELAY: number = 10000
    private readonly MAX_RECONNECT_DELAY: number = 120000 // 2 mins
    private readonly BASE_BACKOFF: number = 10000
    private readonly JITTER_MAX: number = 5000

    private connectionAttempts: number = 0
    private initialConnectionSuccessful: boolean = false
    private _lastWebsocketError: Event | null = null
    private _maxConnectionAttemptsReached: boolean = false
    private _rxStomp: RxStomp | null = null

    public activate(connectionInfo: ConnectionInfo): Promise<ConnectedInfo> {
        return new Promise((resolve, reject): void => {
            // Validate state and short circuit
            if(!connectionInfo){
                reject('You must supply a valid connectionInfo object')
                return
            }

            if (!(connectionInfo.host)) {
                reject('No host provided')
                return
            }

            if(this._rxStomp) {
                reject('Stomp connection already active')
                return
            }

            // we reset most state here so, it will persist on a connection failure
            this.connectionAttempts = 0
            this.initialConnectionSuccessful = false
            this._lastWebsocketError = null
            this._maxConnectionAttemptsReached = false

            const url = 'ws' + (connectionInfo.useSSL ? 's' : '')
                + '://' + connectionInfo.host
                + (connectionInfo.port ? ':' + connectionInfo.port : '') + '/v1'

            this._rxStomp = new RxStomp()

            let connectHeadersInternal: StompHeaders = (connectionInfo.connectHeaders ? connectionInfo.connectHeaders : {})

            //*** Begin Block that handles backoff ***
            this._rxStomp.configure({
                                        brokerURL: url,
                                        connectHeaders: connectHeadersInternal,
                                        heartbeatIncoming: 120000,
                                        heartbeatOutgoing: 30000,
                                        reconnectDelay: 2000, // initial reconnect delay fairly short to fail fast
                                        beforeConnect: async (): Promise<void> => {
                                            // If max connections are set then make sure we have not exceeded that threshold
                                            if(connectionInfo?.maxConnectionAttempts){
                                                this.connectionAttempts++

                                                if(this.connectionAttempts > connectionInfo.maxConnectionAttempts){

                                                    // Reached threshold give up
                                                    this._maxConnectionAttemptsReached = true
                                                    await this.deactivate()

                                                    // If we have not made an initial connection the promise is not yet resolved
                                                    if(!this.initialConnectionSuccessful) {
                                                        let message = (this._lastWebsocketError as any)?.message ? (this._lastWebsocketError as any)?.message : 'UNKNOWN'
                                                        reject(`Max number of reconnection attempts reached. Last WS Error ${message}`)
                                                    }
                                                }
                                            }
                                        }
                                    })

            // Handles Websocket Errors
            this._rxStomp.webSocketErrors$.subscribe(value => {
                this._lastWebsocketError = value

                // An error occurred if the initial connection has been made, Add backoff to reconnect delay
                if(this._rxStomp && this.initialConnectionSuccessful){

                    const currentReconnectDelay = this._rxStomp.stompClient.reconnectDelay

                    if(currentReconnectDelay < this.MAX_RECONNECT_DELAY){

                        const randomJitter = Math.random() * this.JITTER_MAX;
                        this._rxStomp.stompClient.reconnectDelay = currentReconnectDelay + this.BASE_BACKOFF + randomJitter

                        if(currentReconnectDelay < this.MAX_RECONNECT_DELAY){
                            this._rxStomp.stompClient.reconnectDelay = this.MAX_RECONNECT_DELAY
                        }
                    }
                }
            })

            // Handles Successful Connections
            this._rxStomp.connected$.subscribe(() =>{
                // Successful Connection
                if(!this.initialConnectionSuccessful){
                    this.initialConnectionSuccessful = true
                }
                if(this._rxStomp) {
                    // Set connection delay to base
                    this._rxStomp.stompClient.reconnectDelay = this.INITIAL_RECONNECT_DELAY
                }
            })
            //*** End Block that handles backoff ***


            // This subscription is to handle any errors that occur during connection
            const errorSubscription: Subscription = this._rxStomp.stompErrors$.subscribe((value: IFrame) => {
                errorSubscription.unsubscribe()
                const message = value.headers['message']
                this._rxStomp?.deactivate()
                this._rxStomp = null
                reject(message)
            })

            const connectedSubscription: Subscription = this._rxStomp.serverHeaders$.subscribe((value: StompHeaders) => {
                connectedSubscription.unsubscribe()
                let connectedInfoJson: string | undefined = value[EventConstants.CONNECTED_INFO_HEADER]
                if (connectedInfoJson != null) {

                    const connectedInfo: ConnectedInfo = JSON.parse(connectedInfoJson)

                    if (connectedInfo.sessionId != null && connectedInfo.replyToId != null) {

                        // Remove all information originally sent from the connect headers
                        if (connectionInfo.connectHeaders != null) {
                            for (let key in connectionInfo.connectHeaders) {
                                delete connectHeadersInternal[key]
                            }
                        }

                        connectHeadersInternal.session = connectedInfo.sessionId

                        resolve(connectedInfo)
                    } else {
                        reject('Server did not return proper data for successful login')
                    }
                } else {
                    reject('Server did not return proper data for successful login')
                }
            })

            this._rxStomp.activate()
        })
    }

    public async deactivate(force?: boolean): Promise<void> {
        if(this._rxStomp){
            this._rxStomp.stompClient.reconnectDelay = 0
            await this._rxStomp.deactivate({force: force})
            this._rxStomp = null
        }
        return
    }

    /**
     * @return true if this {@link StompConnectionManager} is actively trying to maintain a connection to the Stomp server, false if not.
     */
    public get active(): boolean {
        if(this._rxStomp){
            return true
        }else{
            return false
        }
    }

    /**
     * return true if this {@link StompConnectionManager} is active and has a connection to the stomp server
     */
    public get connected(): boolean {
        return this._rxStomp != null
            && this._rxStomp.connected()
    }

    public get rxStomp(): RxStomp | null {
        return this._rxStomp;
    }

    public get lastWebsocketError(): Event | null{
        return this._lastWebsocketError
    }

    /**
     * This will return true if a {@link ConnectionInfo#maxConnectionAttempts} threshold was set and was reached
     */
    public get maxConnectionAttemptsReached(): boolean{
        return this._maxConnectionAttemptsReached
    }

}
