import {ConnectedInfo, ConnectionInfo} from '../src'
import {AlwaysPullPolicy, GenericContainer, StartedTestContainer} from 'testcontainers'
import {expect} from 'vitest'

/**
 * Logs the failure of a promise and then rethrows the error
 * @param promise to log failure of
 * @param message to log
 */
export async function logFailure<T>(promise: Promise<T>, message: string): Promise<T> {
    try {
        return await promise
    } catch (e) {
        console.error(message, e)
        throw e
    }
}

export function validateConnectedInfo(connectedInfo: ConnectedInfo, roles?: string[]): void{
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

export async function initContinuumGateway(): Promise<{
                                                        testContainer: StartedTestContainer,
                                                        connectionInfo: ConnectionInfo
                                                      }> {
    console.log('Starting Continuum Gateway')
    const testContainer= await new GenericContainer('kinotic/continuum-gateway-server:latest')
        .withExposedPorts(58503)
        .withEnvironment({SPRING_PROFILES_ACTIVE: "clienttest"})
        .withBindMounts([{source:'/tmp/ignite', target:'/workspace/ignite/work', mode:'rw'}])
        .withPullPolicy(new AlwaysPullPolicy())
        .start()
    const connectionInfo = new ConnectionInfo()
    connectionInfo.host = testContainer.getHost()
    connectionInfo.port = testContainer.getMappedPort(58503)
    connectionInfo.maxConnectionAttempts = 3
    connectionInfo.connectHeaders = {login: 'guest', passcode: 'guest'}
    console.log(`Continuum Gateway running at ${connectionInfo.host}:${connectionInfo.port}`)
    return {
        testContainer,
        connectionInfo
    }
}
