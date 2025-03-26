import {Publish} from '../src/index.js'

@Publish("com.example")
export class TestServiceNoScope {
    greet(name: string): string {
        return `Hello, ${name}!`
    }

    async fetchData(id: number): Promise<{ id: number; value: string }> {
        return Promise.resolve({ id, value: `Data for ${id}` })
    }

    combine(a: string, b: number): string {
        return `${a} - ${b}`
    }

    async multiArgs(x: number, y: string, z: boolean): Promise<{ x: number; y: string; z: boolean }> {
        return Promise.resolve({ x, y, z })
    }

    failSync(): string {
        throw new Error("Sync failure")
    }

    async failAsync(): Promise<never> {
        return Promise.reject(new Error("Async failure"))
    }
}
