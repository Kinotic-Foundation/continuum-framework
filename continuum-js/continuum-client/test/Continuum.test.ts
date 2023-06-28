import {beforeAll, describe, it} from 'vitest'
import { Continuum }             from '../src'
import {GenericContainer} from 'testcontainers'

// This is required when running Continuum from node
import { WebSocket } from 'ws'
Object.assign(global, { WebSocket})

describe('Continuum', () => {

    let host!: string
    let port!: number

    beforeAll(async () => {
        const container = await new GenericContainer('kinotic/continuum-gateway-server:latest')
            .withExposedPorts(58503)
            .withEnvironment({ SPRING_PROFILES_ACTIVE: "development" })
            .start()
        host = container.getHost()
        port = container.getMappedPort(58503)
        console.log(`Continuum Gateway running at ${host}:${port}`)
    }, 1000 * 60 * 30) // 30 minutes

    it('should connect', async () => {
        await Continuum.connect(`ws://${host}:${port}/v1`, 'test', 'test')
    })

    it('should disconnect', async () => {
        await Continuum.disconnect()
    })
})
