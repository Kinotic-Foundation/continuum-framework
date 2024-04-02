import {C3Decorator} from '@/api/decorators/C3Decorator'
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
     * The list of Decorators that should be applied to this type
     */
    public decorators?: C3Decorator[] = []

    /**
     * Stores the given value in the functions definitions for this schema
     * If a schema for the name already exists an error will be thrown
     * @param func the function to add
     * @return this
     */
    public addFunction(func: FunctionDefinition): this {
        if(this.functions.has(func)){
            throw new Error(`ServiceDefinition already contains function for name ${func.name}`)
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
     * @return this {@link ServiceDefinition}
     */
    public addDecorator(decorator: C3Decorator): ServiceDefinition {
        if (this.containsDecorator(decorator)) {
            throw new Error(`ServiceDefinition already contains decorator for name ${decorator.type}`);
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
