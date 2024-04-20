import { C3Type } from '@/api/C3Type'
import { C3Decorator } from '@/api/decorators/C3Decorator'

/**
 * Defines a property for a {@link ObjectC3Type}
 * Created by Navíd Mitchell 🤪 on 2/22/24.
 */
export class PropertyDefinition {
    /**
     * The metadata keyword is legal on any {@link PropertyDefinition}. The objects provided must be serializable to JSON.
     * Usually, metadata is for putting things like descriptions or hints for code generators, or other things tools can use.
     */
    public metadata?: {[key: string]: any} = {}

    /**
     * This is the name of the {@link PropertyDefinition} such as 'firstName', 'lastName'
     */
    public name: string | null = null;

    /**
     * This is the {@link C3Type} of this {@link PropertyDefinition}
     */
    public type: C3Type | null = null;

    /**
     * The list of Decorators that should be applied to this {@link PropertyDefinition}
     */
    public decorators?: C3Decorator[] | null = null

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
        let ret = false
        if(this.decorators){
            ret = this.decorators.length > 0
        }
        return ret
    }

    /**
     * Adds a new decorator to this type
     * @param decorator the decorator to add
     * @return this {@link C3Type}
     */
    public addDecorator(decorator: C3Decorator): PropertyDefinition {
        if (this.containsDecorator(decorator)) {
            throw new Error(`PropertyDefinition already contains decorator for name ${decorator.type}`);
        }

        if(!this.decorators){
            this.decorators = []
        }

        this.decorators.push(decorator)
        return this
    }

    /**
     * Finds the first {@link C3Decorator} of the given subclass or null if none are found
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
