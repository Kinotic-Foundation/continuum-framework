import {afterAll, beforeAll, describe, expect, it} from 'vitest'
import {WebSocket} from 'ws'
import {ConnectedInfo, ConnectionInfo, Continuum, ContinuumSingleton} from '../src'
import { GenericContainer, PullPolicy, StartedTestContainer, Wait } from 'testcontainers'
import { logFailure, validateConnectedInfo } from './TestHelper'

// This is required when running Continuum from node
Object.assign(global, { WebSocket})

// These tests live in their own fle because if working improperly, the can cause the test to hang
describe('Continuum Unavailable Tests', () => {

    let container: StartedTestContainer
    let connectionInfo: ConnectionInfo = new ConnectionInfo()

    beforeAll(async () => {
        // Start the Continuum Gateway container
        console.log('Starting Continuum Gateway for sticky session gateway restart reconnection test')
        
        container = await new GenericContainer('kinotic/continuum-gateway-server:latest')
            .withExposedPorts({container: 58503, host: 58590})
            .withEnvironment({SPRING_PROFILES_ACTIVE: "clienttest"})
            .withPullPolicy(PullPolicy.alwaysPull())
            .withWaitStrategy(Wait.forHttp('/', 58503))
            .withName('disable-sticky-session-reconnect-test')
            .start()

        // Create connection info with disableStickySession enabled
        connectionInfo.host = container.getHost()
        connectionInfo.port = 58590
        connectionInfo.maxConnectionAttempts = 10
        connectionInfo.disableStickySession = false
        connectionInfo.connectHeaders = async () => {return {login: 'guest', passcode: 'guest'}}
        connectionInfo.debug = (msg: string): void => { console.log(new Date(), msg) }

        console.log(`Continuum Gateway running at ${connectionInfo.host}:${connectionInfo.port}`)
    }, 1000 * 60 * 10) // 10 minutes

    afterAll(async () => {
        // Clean up
        container.stop({timeout: 60000, remove: true, removeVolumes: true})
    })

    it('should fail fast on connection attempt', async () => {
        const host: string = 'notavailable'
        const port: number = 58503
        console.log(`Trying to Connecting to Unavailable Continuum Gateway`)
        await expect(Continuum.connect({
                                           host:host,
                                           port:port,
                                           maxConnectionAttempts: 3,
                                           connectHeaders:{login: 'guest', passcode: 'guest'}
                                       }))
            .rejects.toThrowError(
                expect.stringMatching(
                    /^Max number of reconnection attempts reached\. Last WS Error getaddrinfo (ENOTFOUND|EAI_AGAIN) notavailable$/
                )
            )

        await expect(Continuum.disconnect()).resolves.toBeUndefined()
    }, 1000 * 60 * 10) // 10 minutes

    it('should connnect to gateway and then fail after reconnection attempts after gateway is offline',
         {"timeout": 1000 * 60 * 2},
         async () => {
        const continuum = new ContinuumSingleton()
        let connectedInfo: ConnectedInfo = await logFailure(continuum.connect(connectionInfo),
                                                            'Failed to connect to Continuum Gateway')
        validateConnectedInfo(connectedInfo)
        console.log(`Continuum Gateway started at ${connectionInfo.host}:${connectionInfo.port}`)

        const promise = new Promise((resolve, reject) => {
            continuum.eventBus.fatalErrors.subscribe((error: Error) => {
                resolve(error)
            })
        })

        // stop the gateway
        await container.stop()

        const error = await logFailure(promise, 'Failed to receive error from fatalErrors observable')

        expect(error).toBeDefined()

        // make sure client was automatically disconnected
        expect(continuum.eventBus.isConnectionActive(),
            'Client to be disconnected').toBe(false)

        await expect(continuum.disconnect()).resolves.toBeUndefined()
    }) 

})
