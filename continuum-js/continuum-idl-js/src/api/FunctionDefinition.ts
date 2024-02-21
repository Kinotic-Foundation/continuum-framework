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
     * This map defines the C3Types that define the arguments for this function.
     * The Key is the argument name the value is the schema for the argument type.
     * Argument names must be unique.
     */
    public arguments: ArgumentDefinition[] = []

    /**
     * The list of Decorators that should be applied to this type
     */
    public decorators?: C3Decorator[] = []

    /**
     * Adds a new argument to this function definition
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
     * Checks if this function has a C3Decorator for the given type
     * @param value the C3Decorator to check for
     */
    public containsDecorator(value: C3Decorator): boolean {
        return this.findDecorator(value) !== null
    }

    /**
     * Adds a new decorator to this function definition
     * @param decorator the decorator to add
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
     * Finds a decorator for the given type
     * @param value the type of decorator to find
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
