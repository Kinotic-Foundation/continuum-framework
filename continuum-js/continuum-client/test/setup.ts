import path from 'node:path'
import {GenericContainer, PullPolicy, StartedTestContainer, Wait} from 'testcontainers'
import {TestProject} from 'vitest/node.js'
import { readFileSync } from 'node:fs'
import { getProperties } from 'properties-file'

let container: StartedTestContainer

// Run once before all tests
export async function setup(project: TestProject) {
    // @ts-ignore
    if(import.meta.env.VITE_USE_GATEWAY_DOCKER === 'true') {
        console.log('Starting Continuum Gateway')

        const props = getProperties(readFileSync(path.resolve('../../', 'gradle.properties')))

        container = await new GenericContainer(`kinotic/continuum-gateway-server:${props['continuumVersion']}`)
            .withExposedPorts(58503)
            .withEnvironment({SPRING_PROFILES_ACTIVE: "clienttest"})
            .withPullPolicy(PullPolicy.alwaysPull())
            .withWaitStrategy(Wait.forHttp('/', 58503))
            .start()

        // @ts-ignore
        project.provide('CONTINUUM_HOST', container.getHost())
        // @ts-ignore
        project.provide('CONTINUUM_PORT', container.getMappedPort(58503))

        console.log('Continuum Gateway started.')
    }else{
        // @ts-ignore
        project.provide('CONTINUUM_HOST', '127.0.0.1')
        // @ts-ignore
        project.provide('CONTINUUM_PORT', 58503)
        console.log('Skipping Continuum Gateway start because VITE_USE_GATEWAY_DOCKER is false')
    }
}

// Run once after all tests
export async function teardown() {
    console.log('Shutting down Continuum Gateway...')
    await container.stop()
    console.log('Continuum Gateway shut down.')
}



