import {AbstractDefinition} from '@/api/AbstractDefinition'
import {ArgumentDefinition} from '@/api/ArgumentDefinition'
import { C3Type } from '@/api/C3Type'
import {C3Decorator} from '@/api/decorators/C3Decorator'
import {MetadataType} from '@/api/HasMetadata'
import { VoidC3Type } from '@/api/VoidC3Type'

/**
 * Provides functionality to define a function with a Continuum schema.
 * Created by navid on 2023-4-13
 */
export class FunctionDefinition extends AbstractDefinition {

    /**
     * This is the C3Type that defines the return type of this function.
     */
    public returnType: C3Type = new VoidC3Type()

    /**
     * This array defines the {@link C3Type}s that define the arguments for this function.
     * Argument names must be unique.
     */
    public arguments: ArgumentDefinition[] = []


    constructor(name: string,
                decorators?: C3Decorator[] | null,
                metadata?: MetadataType | null) {
        super(name, decorators, metadata)
    }

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

}
