import {C3Type} from '@/api/C3Type'
import {C3Decorator} from '@/api/decorators/C3Decorator'

export class ArgumentDefinition {
    /**
     * The name of this {@link ArgumentDefinition}
     */
    public name: string

    /**
     * The type of this {@link ArgumentDefinition}
     */
    public type: C3Type

    /**
     * The list of Decorators that should be applied to this type
     */
    public decorators?: C3Decorator[] = []

    public constructor(name: string, type: C3Type, decorators?: C3Decorator[]) {
        this.name = name
        this.type = type
        this.decorators = decorators ? decorators : []
    }

    /**
     * Checks if this function has a C3Decorator for the given type
     * @param value
     */
    public containsDecorator(value: any): boolean {
        return this.findDecorator(value) !== null
    }

    /**
     * Adds a decorator to this argument definition
     * @param decorator The decorator to add
     */
    public addDecorator(decorator: any): ArgumentDefinition {
        if (this.containsDecorator(decorator)) {
            throw new Error(`ArgumentDefinition already contains decorator for name ${decorator.type}`);
        }
        if(!this.decorators){
            this.decorators = []
        }
        this.decorators.push(decorator)
        return this
    }

    /**
     * Finds a decorator for the given type if it exists
     * @param value The type to find
     */
    public findDecorator(value: any): any | null {
        let ret: any | null = null
        if (this.decorators) {
            for (const decorator of this.decorators) {
                if(decorator.type === value.type) {
                    ret = decorator
                    break
                }
            }
        }
        return ret
    }
}
