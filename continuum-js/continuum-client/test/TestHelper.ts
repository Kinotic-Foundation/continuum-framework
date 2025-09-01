import {ConnectedInfo, ConnectionInfo} from '../src'
import {expect, inject} from 'vitest'

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

export async function initContinuumGateway(disableStickySession: boolean = false): Promise<{
                                                        connectionInfo: ConnectionInfo
                                                      }> {
    const connectionInfo = new ConnectionInfo()
    // @ts-ignore
    connectionInfo.host = inject('CONTINUUM_HOST')
    // @ts-ignore
    connectionInfo.port = inject('CONTINUUM_PORT')
    connectionInfo.maxConnectionAttempts = 3
    connectionInfo.connectHeaders = {login: 'guest', passcode: 'guest'}
    connectionInfo.debug = (msg: string): void => {
        console.log(new Date(), msg)
    }
    connectionInfo.disableStickySession = disableStickySession
    console.log(`Continuum Gateway running at ${connectionInfo.host}:${connectionInfo.port}`)
    return {
        connectionInfo
    }
}
