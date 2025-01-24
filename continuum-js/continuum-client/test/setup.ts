import {GenericContainer, PullPolicy, StartedTestContainer, Wait} from 'testcontainers'
import {TestProject} from 'vitest/node.js'

let container: StartedTestContainer

// Run once before all tests
export async function setup(project: TestProject) {
    console.log('Starting Continuum Gateway')
    container = await new GenericContainer('kinotic/continuum-gateway-server:latest')
        .withExposedPorts(58503)
        .withEnvironment({SPRING_PROFILES_ACTIVE: "clienttest"})
        .withBindMounts([{source: '/tmp/ignite', target: '/workspace/ignite/work', mode: 'rw'}])
        .withPullPolicy(PullPolicy.alwaysPull())
        .withWaitStrategy(Wait.forHttp('/', 58503))
        .start()
    project.provide('CONTINUUM_HOST', container.getHost())
    project.provide('CONTINUUM_PORT', container.getMappedPort(58503))
    console.log('Continuum Gateway started.')
}

// Run once after all tests
export async function teardown() {
    console.log('Shutting down Continuum Gateway...')
    await container.stop()
    console.log('Continuum Gateway shut down.')
}



