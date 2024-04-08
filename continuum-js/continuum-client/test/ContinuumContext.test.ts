import {describe, expect, it, beforeAll, afterAll} from 'vitest'
import {ConnectionInfo, Continuum, ConnectedInfo, ContinuumSingleton} from '../src'
import {WebSocket} from 'ws'
import {logFailure, validateConnectedInfo, initContinuumGateway} from './TestHelper'
import {TEST_SERVICE} from './ITestService'
import {StartedTestContainer} from 'testcontainers'

// This is required when running Continuum from node
Object.assign(global, { WebSocket})

describe('Continuum RPC Tests', () => {
    let container: StartedTestContainer

    beforeAll(async () => {
        const {testContainer, connectionInfo} = await initContinuumGateway()
        container = testContainer
        let connectedInfo: ConnectedInfo = await logFailure(Continuum.connect(connectionInfo), 'Failed to connect to Continuum Gateway')
        validateConnectedInfo(connectedInfo)
    }, 1000 * 60 * 10) // 10 minutes

    afterAll(async () =>{
        await expect(Continuum.disconnect()).resolves.toBeUndefined()
        await container.stop()
    })


    it('should allow context switching', async () =>{
        const uuid1 = await TEST_SERVICE.getTestUUID()
        expect(uuid1).toBeDefined()
        await expect(TEST_SERVICE.getTestUUID()).resolves.toEqual(uuid1) // static sanity check

        const {testContainer: testContainer2, connectionInfo: connectionInfo2} = await initContinuumGateway()
        const continuum2 = new ContinuumSingleton()
        let connectedInfo2: ConnectedInfo = await logFailure(continuum2.connect(connectionInfo2), 'Failed to connect to Continuum Gateway')
        validateConnectedInfo(connectedInfo2)

        await continuum2.execute(async() => {
            await expect(TEST_SERVICE.getTestUUID()).resolves.not.toEqual(uuid1)
        })

        await expect(continuum2.disconnect()).resolves.toBeUndefined()
        await testContainer2.stop()

    }, 1000 * 60 * 10) // 10 minutes

})
