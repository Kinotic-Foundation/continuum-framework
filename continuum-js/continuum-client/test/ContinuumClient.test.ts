import {StartedTestContainer} from 'testcontainers'
import {afterAll, beforeAll, describe, expect, it} from 'vitest'
import {WebSocket} from 'ws'
import {ConnectedInfo, Continuum, ContinuumSingleton, Event, EventConstants, ParticipantConstants} from '../src'
import {initContinuumGateway, logFailure, validateConnectedInfo} from './TestHelper'

// This is required when running Continuum from node
Object.assign(global, { WebSocket})

describe('Continuum Client Tests', () => {

    let host: string = '127.0.0.1'
    let port: number = 58503

    beforeAll(async () => {
        const connectionInfo = (await initContinuumGateway()).connectionInfo
        host = connectionInfo.host
        port = connectionInfo.port
    }, 1000 * 60 * 10) // 10 minutes

    afterAll(async () => {
        await expect(Continuum.disconnect()).resolves.toBeUndefined()
    })

    async function connectToContinuum(continuum: ContinuumSingleton) {
        return await logFailure(continuum.connect({
                                                      host: host,
                                                      port: port,
                                                      maxConnectionAttempts: 3,
                                                      connectHeaders: {
                                                          login: 'guest',
                                                          passcode: 'guest'
                                                      }
                                                  }),
                                'Failed to connect to Continuum Gateway')
    }

    it('should connect and disconnect', async () => {
        const continuum = new ContinuumSingleton()
        const connectedInfo = await connectToContinuum(continuum)
        validateConnectedInfo(connectedInfo)

        await expect(continuum.disconnect()).resolves.toBeUndefined()
    })

    it('should connect and disconnect multiple times', async () => {
        const continuum = new ContinuumSingleton()

        console.log(`Connecting to Continuum Gateway running at ${host}:${port} the first time`)
        let connectedInfo = await connectToContinuum(continuum)
        validateConnectedInfo(connectedInfo)
        await expect(continuum.disconnect()).resolves.toBeUndefined()

        console.log(`Connecting to Continuum Gateway running at ${host}:${port} the second time`)
        connectedInfo = await connectToContinuum(continuum)
        validateConnectedInfo(connectedInfo)
        await expect(continuum.disconnect()).resolves.toBeUndefined()

        console.log(`Connecting to Continuum Gateway running at ${host}:${port} the third time`)
        connectedInfo = await connectToContinuum(continuum)
        validateConnectedInfo(connectedInfo)
        await expect(continuum.disconnect()).resolves.toBeUndefined()
    })

    it('should allow continuum CLI to connect but not send any data', async () => {
        const continuum = new ContinuumSingleton()
        console.log(`Connecting to Continuum Gateway running at ${host}:${port}`)

        let connectedInfo: ConnectedInfo = await logFailure(continuum.connect(
                                                                {
                                                                    host:host,
                                                                    port:port,
                                                                    connectHeaders:{login: ParticipantConstants.CLI_PARTICIPANT_ID}
                                                                }),
                                                            'Failed to connect to Continuum Gateway')

        validateConnectedInfo(connectedInfo, ['ANONYMOUS'])

        const promise = new Promise((resolve, reject) => {
            continuum.eventBus.fatalErrors.subscribe((error: Error) => {
                resolve(error)
            })
        })

        console.log('Sending invalid event to continuum CLI')
        continuum.eventBus.send(new Event(EventConstants.SERVICE_DESTINATION_PREFIX+ 'blah'))

        const error = await logFailure(promise, 'Failed to receive error from fatalErrors observable')

        expect(error).toBeDefined()

        // make sure client was automatically disconnected
        expect(continuum.eventBus.isConnectionActive(),
            'Client to be disconnected').toBe(false)

        await expect(continuum.disconnect()).resolves.toBeUndefined()

    })

    it('should allow connection with session id', async () => {
        const continuum = new ContinuumSingleton()
        let connectedInfo: ConnectedInfo = await logFailure(continuum.connect(
                                                                {
                                                                    host:host,
                                                                    port:port,
                                                                    connectHeaders:{login: ParticipantConstants.CLI_PARTICIPANT_ID}
                                                                }),
                                                            'Failed to connect to Continuum Gateway')
        validateConnectedInfo(connectedInfo, ['ANONYMOUS'])

        // We use force here true. Otherwise, the server will clean up the session
        await expect(continuum.disconnect(true)).resolves.toBeUndefined()

        connectedInfo = await logFailure(continuum.connect({
                host:host,
                port:port,
                connectHeaders:{session: connectedInfo.sessionId}
            }),
            'Failed to connect to Continuum Gateway with session id')

        validateConnectedInfo(connectedInfo, ['ANONYMOUS'])

        await expect(continuum.disconnect()).resolves.toBeUndefined()

    })

})
