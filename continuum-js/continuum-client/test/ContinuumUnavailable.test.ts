import {describe, expect, it, beforeAll, afterAll} from 'vitest'
import {ConnectedInfo, Continuum, Event, EventConstants, ParticipantConstants} from '../src'
import {WebSocket} from 'ws'
import {initContinuumGateway, logFailure, validateConnectedInfo} from './TestHelper'
import {TEST_SERVICE} from './ITestService'
import {AlwaysPullPolicy, GenericContainer, StartedTestContainer} from 'testcontainers'

// This is required when running Continuum from node
Object.assign(global, { WebSocket})

// These tests live in their own fle because if working improperly, the can cause the test to hang
describe('Continuum Unavailable Tests', () => {

    it('should connect and disconnect', async () => {
        const host: string = 'notavailable'
        const port: number = 58503
        console.log(`Trying to Connecting to Unavailable Continuum Gateway`)
        await  expect(Continuum.connect({
                                    host:host,
                                    port:port,
                                    maxConnectionAttempts: 3,
                                    connectHeaders:{login: 'guest', passcode: 'guest'}
                                })).rejects.toEqual('Max number of reconnection attempts reached. Last WS Error getaddrinfo ENOTFOUND notavailable')

        await expect(Continuum.disconnect()).resolves.toBeUndefined()
    }, 1000 * 60 * 10) // 10 minutes

})
