import {describe, expect, it, beforeAll, afterAll} from 'vitest'
import {ConnectionInfo, Continuum, ConnectedInfo, IEvent, Event, EventConstants} from '../src'
import {WebSocket} from 'ws'
import {logFailure, validateConnectedInfo, initContinuumGateway} from './TestHelper'
import {StartedTestContainer} from 'testcontainers'
import { v4 as uuidv4 } from 'uuid'

// This is required when running Continuum from node
Object.assign(global, { WebSocket})

describe('Continuum RPC Tests', () => {
    let connectionInfo: ConnectionInfo = new ConnectionInfo()
    let container: StartedTestContainer
    let connectedInfo: ConnectedInfo

    beforeAll(async () => {
        container = await initContinuumGateway()
        connectionInfo.host = container.getHost()
        connectionInfo.port = container.getMappedPort(58503)
        connectionInfo.maxConnectionAttempts = 3
        // connectionInfo.host = '127.0.0.1'
        // connectionInfo.port = 58503
        connectionInfo.connectHeaders = {login: 'guest', passcode: 'guest'}
        console.log(`Continuum Gateway running at ${connectionInfo.host}:${connectionInfo.port}`)

        connectedInfo = await logFailure(Continuum.connect(connectionInfo), 'Failed to connect to Continuum Gateway')

        validateConnectedInfo(connectedInfo)
    }, 1000 * 60 * 10) // 10 minutes

    afterAll(async () =>{
        await expect(Continuum.disconnect()).resolves.toBeUndefined()
        await container.stop()
    })

    it('should fail invalid service request', async () => {
        const toSend: IEvent = new Event('srv://org.kinotic.continuum.gatewayserver.clienttest.ITestService/testMethodWithString')
        toSend.setHeader(EventConstants.REPLY_TO_HEADER, null)
        toSend.setHeader(EventConstants.CONTENT_TYPE_HEADER, EventConstants.CONTENT_JSON)
        const correlationId = uuidv4()
        toSend.setHeader(EventConstants.CORRELATION_ID_HEADER, correlationId)
        toSend.setDataString('["Bob"]')

        let errorEncountered = new Promise<Error>((resolve, reject) => {
            Continuum.eventBus.fatalErrors.subscribe((error: Error) => {
                reject(error)
            })
        })

        Continuum.eventBus.send(toSend)

        await expect(errorEncountered).rejects.toThrowError('reply-to header invalid, scheme: null is not valid for service requests')

        expect(Continuum.eventBus.isConnectionActive()).toBeFalsy()

    })

})
