import {Publish, Scope} from '../src/index.js'

@Publish("com.example")
export class TestServiceWithScope {
    @Scope
    scope: string = "tenant"

    greet(name: string): string {
        return `Hello, ${name} from ${this.scope}!`
    }

    async fetchData(id: number): Promise<{ id: number; value: string; scope: string }> {
        return Promise.resolve({ id, value: `Data for ${id}`, scope: this.scope })
    }

    combine(a: string, b: number): string {
        return `${a} - ${b} in ${this.scope}`
    }

    async multiArgs(x: number, y: string, z: boolean): Promise<{ x: number; y: string; z: boolean; scope: string }> {
        return Promise.resolve({ x, y, z, scope: this.scope })
    }

    failSync(): string {
        throw new Error("Scoped sync failure")
    }

    async failAsync(): Promise<never> {
        return Promise.reject(new Error("Scoped async failure"))
    }
}
