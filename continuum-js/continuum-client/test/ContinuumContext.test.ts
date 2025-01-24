import './Instrumentation'
import {StartedTestContainer} from 'testcontainers'
import {afterAll, beforeAll, describe, expect, it} from 'vitest'
import {WebSocket} from 'ws'
import {ConnectedInfo, Continuum, ContinuumSingleton} from '../src'
import {TEST_SERVICE} from './ITestService'
import {initContinuumGateway, logFailure, validateConnectedInfo} from './TestHelper'

// This is required when running Continuum from node
Object.assign(global, { WebSocket})

describe('Continuum Context Tests', () => {
    let container: StartedTestContainer

    beforeAll(async () => {
        const {connectionInfo} = await initContinuumGateway()
        let connectedInfo: ConnectedInfo = await logFailure(Continuum.connect(connectionInfo), 'Failed to connect to Continuum Gateway')
        validateConnectedInfo(connectedInfo)
    }, 1000 * 60 * 10) // 10 minutes

    afterAll(async () =>{
        await expect(Continuum.disconnect()).resolves.toBeUndefined()
    })


    it('should allow context switching', async () =>{
        const uuid1 = await TEST_SERVICE.getTestUUID()
        expect(uuid1).toBeDefined()
        await expect(TEST_SERVICE.getTestUUID()).resolves.toEqual(uuid1) // static sanity check

        const {connectionInfo: connectionInfo2} = await initContinuumGateway()
        const continuum2 = new ContinuumSingleton()
        let connectedInfo2: ConnectedInfo = await logFailure(continuum2.connect(connectionInfo2), 'Failed to connect to Continuum Gateway')
        validateConnectedInfo(connectedInfo2)

        await continuum2.execute(async() => {
            await expect(TEST_SERVICE.getTestUUID()).resolves.not.toEqual(uuid1)
        })

        await expect(continuum2.disconnect()).resolves.toBeUndefined()
    }, 1000 * 60 * 10) // 10 minutes

})
