import {beforeAll, describe, it, expect} from 'vitest'
import {Continuum, EventConstants} from '../src'
import {GenericContainer} from 'testcontainers'
import { WebSocket } from 'ws'
import { ConnectedInfo, ParticipantConstants, Event } from '../src'
import {logFailure} from './TestHelper'

// This is required when running Continuum from node
Object.assign(global, { WebSocket})

describe('Continuum Client Tests', () => {

    let host: string = '127.0.0.1'
    let port: number = 58503

    // beforeAll(async () => {
    //     console.log('Starting Continuum Gateway')
    //     const container = await new GenericContainer('kinotic/continuum-gateway-server:latest')
    //         .withExposedPorts(58503)
    //         .withEnvironment({ SPRING_PROFILES_ACTIVE: "development" })
    //         .start()
    //     host = container.getHost()
    //     port = container.getMappedPort(58503)
    //     console.log(`Continuum Gateway running at ${host}:${port}`)
    // }, 1000 * 60 * 10) // 10 minutes

    it('should connect and disconnect', async () => {
        const connectedInfo: ConnectedInfo = await logFailure(Continuum.connect(`ws://${host}:${port}/v1`,
                'guest',
                'guest'),
            'Failed to connect to Continuum Gateway')
        validateConnectedInfo(connectedInfo)

        await expect(Continuum.disconnect()).resolves.toBeUndefined()
    })

    it('should connect and disconnect multiple times', async () => {
        console.log(`Connecting to Continuum Gateway running at ${host}:${port} the first time`)
        let connectedInfo: ConnectedInfo = await logFailure(Continuum.connect(`ws://${host}:${port}/v1`,
                'guest',
                'guest'),
            'Failed to connect to Continuum Gateway')
        validateConnectedInfo(connectedInfo)
        await expect(Continuum.disconnect()).resolves.toBeUndefined()

        console.log(`Connecting to Continuum Gateway running at ${host}:${port} the second time`)
        connectedInfo = await logFailure(Continuum.connect(`ws://${host}:${port}/v1`,
                'guest',
                'guest'),
            'Failed to connect to Continuum Gateway')
        validateConnectedInfo(connectedInfo)
        await expect(Continuum.disconnect()).resolves.toBeUndefined()

        console.log(`Connecting to Continuum Gateway running at ${host}:${port} the third time`)
        connectedInfo = await logFailure(Continuum.connect(`ws://${host}:${port}/v1`,
                'guest',
                'guest'),
            'Failed to connect to Continuum Gateway')
        validateConnectedInfo(connectedInfo)
        await expect(Continuum.disconnect()).resolves.toBeUndefined()
    })

    it('should allow continuum CLI to connect but not send any data', async () => {
        console.log(`Connecting to Continuum Gateway running at ${host}:${port}`)

        let connectedInfo: ConnectedInfo = await Continuum.connect(`ws://${host}:${port}/v1`,
            ParticipantConstants.CLI_PARTICIPANT_ID,
            '')

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
        let connectedInfo: ConnectedInfo = await logFailure(Continuum.connect(`ws://${host}:${port}/v1`,
                'guest',
                'guest'),
            'Failed to connect to Continuum Gateway')
        validateConnectedInfo(connectedInfo)

        await expect(Continuum.disconnect(true)).resolves.toBeUndefined()

        connectedInfo = await logFailure(Continuum.connectAdvanced(`ws://${host}:${port}/v1`,{session: connectedInfo.sessionId}),
            'Failed to connect to Continuum Gateway with session id')

        validateConnectedInfo(connectedInfo)

        await expect(Continuum.disconnect()).resolves.toBeUndefined()

    })

    // it('should allow continuum CLI to connect and use the LogManager', async () => {
    //     const connectedInfo: ConnectedInfo = await logFailure(Continuum.connect(`ws://${host}:${port}/v1`,
    //             'guest',
    //             'guest'),
    //         'Failed to connect to Continuum Gateway')
    //     validateConnectedInfo(connectedInfo)
    //     TODO: add the ability to get the available node ids
    //     logManager.loggers()
    //
    //     await expect(Continuum.disconnect()).resolves.toBeUndefined()
    // })

    function validateConnectedInfo(connectedInfo: ConnectedInfo, roles?: string[]): void{
        expect(connectedInfo).toBeDefined()
        expect(connectedInfo.sessionId).toBeDefined()
        expect(connectedInfo.participant.id).toBeDefined()
        expect(connectedInfo.participant.roles).toBeDefined()
        expect(connectedInfo.participant.roles.length).toBe(1)
        if(roles){
            expect(connectedInfo.participant.roles).toEqual(roles)
        }else {
            expect(connectedInfo.participant.roles[0]).toBe('ADMIN')
        }
    }
})
