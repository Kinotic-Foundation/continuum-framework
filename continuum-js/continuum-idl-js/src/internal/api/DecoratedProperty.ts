import {C3Decorator} from "@/api/decorators/C3Decorator"


export class DecoratedProperty {

    protected jsonPath: string
    protected decoratedTypeClass: string
    protected decorators: C3Decorator[]

    constructor(jsonPath: string, decoratedTypeClass: string, decorators: C3Decorator[]) {
        this.jsonPath = jsonPath
        this.decoratedTypeClass = decoratedTypeClass
        this.decorators = decorators
    }
}