import { C3Type } from '@/api/C3Type'
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
    public arguments: Map<string, C3Type> = new Map<string, C3Type>()

    public addArgument(name: string, c3Type: C3Type): FunctionDefinition {
        if (this.arguments.has(name)) {
            throw new Error(`An argument already exists with the name ${name}`)
        }

        this.arguments.set(name, c3Type)
        return this
    }

}