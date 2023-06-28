import { FunctionDefinition } from '@/api/FunctionDefinition'

/**
 * Provides functionality to define an interface with a Continuum schema.
 * <p>
 * Created by navid on 2023-4-13.
 */
export class ServiceDefinition {

    /**
     * The namespace this {@link ServiceDefinition} belongs to
     */
    public namespace: string = ''

    /**
     * The name of this {@link ServiceDefinition}
     */
    public name: string = ''

    /**
     * This defines {@link FunctionDefinition}'s for this {@link ServiceDefinition}
     * The key is the function name and the value is the schema that defines the function
     */
    public functions: Set<FunctionDefinition> = new Set<FunctionDefinition>()

    /**
     * Stores the given value in the functions definitions for this schema
     * If a schema for the name already exists an error will be thrown
     * @param name the name of the function
     * @param schema {@link FunctionDefinition} defining the function
     * @return this
     */
    public addFunction(func: FunctionDefinition): this {
        if(this.functions.has(func)){
            throw new Error(`InterfaceJsonSchema already contains function for name ${func.name}`)
        }
        this.functions.add(func)
        return this
    }

    /**
     * The URN is the namespace + "." + name
     * @return the urn for this {@link ServiceDefinition}
     */
    public getUrn(): string {
        return this.namespace + "." + this.name
    }
}
