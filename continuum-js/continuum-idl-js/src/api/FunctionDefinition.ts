import {ArgumentDefinition} from '@/api/ArgumentDefinition'
import { C3Type } from '@/api/C3Type'
import {C3Decorator} from '@/api/decorators/C3Decorator'
import { VoidC3Type } from '@/api/VoidC3Type'

/**
 * Provides functionality to define a function with a Continuum schema.
 * Created by navid on 2023-4-13
 */
export class FunctionDefinition {

    /**
     * The name of this {@link FunctionDefinition}
     */
    public name: string = ''

    /**
     * This is the C3Type that defines the return type of this function.
     */
    public returnType: C3Type = new VoidC3Type()

    /**
     * This array defines the {@link C3Type}s that define the arguments for this function.
     * Argument names must be unique.
     */
    public arguments: ArgumentDefinition[] = []

    /**
     * The list of Decorators that should be applied to this type
     */
    public decorators?: C3Decorator[] = []

    /**
     * Adds a new argument to this function definition
     * Arguments must have unique names
     * @param name the name of the argument
     * @param c3Type the type of the argument
     * @param decorators the decorators to apply to the argument
     */
    public addArgument(name: string, c3Type: C3Type, decorators?: C3Decorator[]): FunctionDefinition {
        const arg = new ArgumentDefinition(name, c3Type, decorators)
        if(this.arguments.find((value) => value.name === name)){
            throw new Error(`FunctionDefinition already contains argument for name ${name}`);
        }
        this.arguments.push(arg)
        return this
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
     * @return this {@link FunctionDefinition}
     */
    public addDecorator(decorator: C3Decorator): FunctionDefinition {
        if (this.containsDecorator(decorator)) {
            throw new Error(`FunctionDefinition already contains decorator for name ${decorator.type}`);
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
