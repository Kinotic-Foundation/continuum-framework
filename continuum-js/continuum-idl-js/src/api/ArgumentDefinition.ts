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
     * Checks if this type has a {@link C3Decorator} for the given type
     * @param value the {@link C3Decorator} to check for
     * @return true if the type has a {@link C3Decorator} for the given type false if not
     */
    public containsDecorator(value: C3Decorator): boolean {
        return this.findDecorator(value) !== null
    }

    /**
     * Checks if this type has any {@link C3Decorator}
     * @return true if any {@link C3Decorator}s are present false if not
     */
    public hasDecorators(): boolean {
        return this.decorators != null && this.decorators.length > 0
    }

    /**
     * Adds a new decorator to this type
     * @param decorator the decorator to add
     * @return this {@link ArgumentDefinition}
     */
    public addDecorator(decorator: C3Decorator): ArgumentDefinition {
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
     * @param value the type to find
     * @return the decorator if it exists null if not
     */
    public findDecorator(value: C3Decorator): C3Decorator | null {
        let ret: C3Decorator | null = null
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
