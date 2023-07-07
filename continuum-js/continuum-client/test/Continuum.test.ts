import {beforeAll, describe, it, expect} from 'vitest'
import { Continuum }             from '../src'
import {GenericContainer} from 'testcontainers'

// This is required when running Continuum from node
import { WebSocket } from 'ws'
Object.assign(global, { WebSocket})

describe('Continuum', () => {

    let host!: string
    let port!: number

    beforeAll(async () => {
        console.log('Starting Continuum Gateway')
        const container = await new GenericContainer('kinotic/continuum-gateway-server:latest')
            .withExposedPorts(58503)
            .withEnvironment({ SPRING_PROFILES_ACTIVE: "development" })
            .start()
        host = container.getHost()
        port = container.getMappedPort(58503)
        console.log(`Continuum Gateway running at ${host}:${port}`)
    }, 1000 * 60 * 30) // 30 minutes

    it('should connect and disconnect', async () => {
        await expect(Continuum.connect(`ws://${host}:${port}/v1`, 'test', 'test')).resolves.toBeUndefined()
        await expect(Continuum.disconnect()).resolves.toBeUndefined()
    })

    it('should connect and disconnect multiple times', async () => {
        console.log(`Connecting to Continuum Gateway running at ${host}:${port} the first time`)
        await expect(Continuum.connect(`ws://${host}:${port}/v1`, 'admin', 'structures')).resolves.toBeUndefined()
        await expect(Continuum.disconnect()).resolves.toBeUndefined()

        console.log(`Connecting to Continuum Gateway running at ${host}:${port} the second time`)
        await expect(Continuum.connect(`ws://${host}:${port}/v1`, 'admin', 'structures')).resolves.toBeUndefined()
        await expect(Continuum.disconnect()).resolves.toBeUndefined()

        console.log(`Connecting to Continuum Gateway running at ${host}:${port} the third time`)
        await expect(Continuum.connect(`ws://${host}:${port}/v1`, 'admin', 'structures')).resolves.toBeUndefined()
        await expect(Continuum.disconnect()).resolves.toBeUndefined()
    })
})
