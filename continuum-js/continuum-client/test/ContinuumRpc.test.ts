import {nodeSdk} from './instrumentation'
import {afterAll, beforeAll, describe, expect, it} from 'vitest'
import {WebSocket} from 'ws'
import {ConnectedInfo, Continuum} from '../src'
import {NON_EXISTENT_SERVICE} from './INonExistentService'
import {TEST_SERVICE} from './ITestService'
import {initContinuumGateway, logFailure, validateConnectedInfo} from './TestHelper'


// This is required when running Continuum from node
Object.assign(global, { WebSocket})

describe('Continuum RPC Tests', () => {

    beforeAll(async () => {
        const {connectionInfo} = await initContinuumGateway()
        let connectedInfo: ConnectedInfo = await logFailure(Continuum.connect(connectionInfo), 'Failed to connect to Continuum Gateway')
        validateConnectedInfo(connectedInfo)
    }, 1000 * 60 * 5) // 5 minutes

    afterAll(async () =>{
        await expect(Continuum.disconnect()).resolves.toBeUndefined()
        await nodeSdk.shutdown().catch(console.error)
        console.log('Node SDK shutdown')
    }, 1000 * 60 * 5) // 5 minutes


    it('should execute method with string parameter', async () =>{
        await expect(TEST_SERVICE.testMethodWithString("Bob")).resolves.toBe("Hello Bob")
    })

    it('should return missing method error', async () => {
        await expect(TEST_SERVICE.testMissingMethod()).rejects.toThrowError('No method could be resolved for methodId /testMissingMethod')
    })

    it('should return missing service error', async () => {
        await expect(NON_EXISTENT_SERVICE.probablyNotHome()).rejects.toThrowError('(NO_HANDLERS,-1) No handlers for address srv://com.namespace.NonExistentService')
    })

})
