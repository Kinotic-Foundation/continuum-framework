import {describe, expect, it, beforeAll, afterAll} from 'vitest'
import {ConnectedInfo, Continuum, Event, EventConstants, ParticipantConstants} from '../src'
import {WebSocket} from 'ws'
import {initContinuumGateway, logFailure, validateConnectedInfo} from './TestHelper'
import {TEST_SERVICE} from './ITestService'
import {AlwaysPullPolicy, GenericContainer, StartedTestContainer} from 'testcontainers'

// This is required when running Continuum from node
Object.assign(global, { WebSocket})

describe('Continuum Client Tests', () => {

    let host: string = '127.0.0.1'
    let port: number = 58503
    let container: StartedTestContainer
    beforeAll(async () => {
        console.log('Starting Continuum Gateway')
        container = await initContinuumGateway()
        host = container.getHost()
        port = container.getMappedPort(58503)
        console.log(`Continuum Gateway running at ${host}:${port}`)
    }, 1000 * 60 * 10) // 10 minutes

    afterAll(async () => {
        await container.stop()
    })

    it('should connect and disconnect', async () => {
        const connectedInfo: ConnectedInfo = await logFailure(Continuum.connect({
                host:host,
                port:port,
                connectHeaders:{login: 'guest', passcode: 'guest'}
        }),
            'Failed to connect to Continuum Gateway')
        validateConnectedInfo(connectedInfo)

        await expect(Continuum.disconnect()).resolves.toBeUndefined()
    })

    it('should connect and disconnect multiple times', async () => {
        console.log(`Connecting to Continuum Gateway running at ${host}:${port} the first time`)
        let connectedInfo: ConnectedInfo = await logFailure(Continuum.connect({
                host:host,
                port:port,
                connectHeaders:{login: 'guest', passcode: 'guest'}
        }),
            'Failed to connect to Continuum Gateway')
        validateConnectedInfo(connectedInfo)
        await expect(Continuum.disconnect()).resolves.toBeUndefined()

        console.log(`Connecting to Continuum Gateway running at ${host}:${port} the second time`)
        connectedInfo = await logFailure(Continuum.connect({
                host:host,
                port:port,
                connectHeaders:{login: 'guest', passcode: 'guest'}
        }),
            'Failed to connect to Continuum Gateway')
        validateConnectedInfo(connectedInfo)
        await expect(Continuum.disconnect()).resolves.toBeUndefined()

        console.log(`Connecting to Continuum Gateway running at ${host}:${port} the third time`)
        connectedInfo = await logFailure(Continuum.connect({
                host:host,
                port:port,
                connectHeaders:{login: 'guest', passcode: 'guest'}
        }),
            'Failed to connect to Continuum Gateway')
        validateConnectedInfo(connectedInfo)
        await expect(Continuum.disconnect()).resolves.toBeUndefined()
    })

    it('should allow continuum CLI to connect but not send any data', async () => {
        console.log(`Connecting to Continuum Gateway running at ${host}:${port}`)

        let connectedInfo: ConnectedInfo = await Continuum.connect(
            {
                host:host,
                port:port,
                connectHeaders:{login: ParticipantConstants.CLI_PARTICIPANT_ID}
            })

        validateConnectedInfo(connectedInfo, ['ANONYMOUS'])

        const promise = new Promise((resolve, reject) => {
            Continuum.eventBus.fatalErrors.subscribe((error: Error) => {
                resolve(error)
            })
        })

        console.log('Sending invalid event to continuum CLI')
        Continuum.eventBus.send(new Event(EventConstants.SERVICE_DESTINATION_PREFIX+ 'blah'))

        const error = await logFailure(promise, 'Failed to receive error from fatalErrors observable')

        expect(error).toBeDefined()

        // make sure client was automatically disconnected
        expect(Continuum.eventBus.isConnectionActive(),
            'Client to be disconnected').toBe(false)

        await expect(Continuum.disconnect()).resolves.toBeUndefined()

    })

    it('should allow connection with session id', async () => {
        let connectedInfo: ConnectedInfo = await logFailure(Continuum.connect(
                {
                    host:host,
                    port:port,
                    connectHeaders:{login: ParticipantConstants.CLI_PARTICIPANT_ID}
                }),
            'Failed to connect to Continuum Gateway')
        validateConnectedInfo(connectedInfo, ['ANONYMOUS'])

        // We use force here true. Otherwise, the server will clean up the session
        await expect(Continuum.disconnect(true)).resolves.toBeUndefined()

        connectedInfo = await logFailure(Continuum.connect({
                host:host,
                port:port,
                connectHeaders:{session: connectedInfo.sessionId}
            }),
            'Failed to connect to Continuum Gateway with session id')

        validateConnectedInfo(connectedInfo, ['ANONYMOUS'])

        await expect(Continuum.disconnect()).resolves.toBeUndefined()

    })

})
