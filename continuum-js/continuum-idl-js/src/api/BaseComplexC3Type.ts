import {BaseC3Type} from '@/api/BaseC3Type'
import {ComplexC3Type} from '@/api/ComplexC3Type'
import {C3Decorator} from '@/api/decorators/C3Decorator'
import {HasDecorators} from '@/api/HasDecorators'
import {MetadataType} from '@/api/HasMetadata'

export class BaseComplexC3Type extends BaseC3Type implements ComplexC3Type {

    /**
     * This is the name of the {@link EnumC3Type} such as "EventType"
     */
    public name: string

    /**
     * The namespace that this {@link EnumC3Type} belongs to
     */
    public namespace?: string | null = null

    /**
     * The list of Decorators that should be applied to this type
     */
    public decorators?: C3Decorator[] | null = null

    /**
     * The metadata keyword is legal on any {@link AbstractDefinition}, The objects provided must be serializable to JSON.
     * Usually, metadata is for putting things like descriptions or hints for code generators, or other things tools can use.
     */
    public metadata?: MetadataType | null

    constructor(type: string,
                name: string,
                namespace?: string | null,
                decorators?: C3Decorator[] | null,
                metadata?: MetadataType | null) {
        super(type)
        this.name = name
        this.namespace = namespace
        this.decorators = decorators
        this.metadata = metadata
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
        let ret = false
        if(this.decorators){
            ret = this.decorators.length > 0
        }
        return ret
    }

    /**
     * Adds a new decorator to this type
     * @param decorator the decorator to add
     * @return this {@link C3Base}
     */
    public addDecorator(decorator: C3Decorator): HasDecorators {
        if(!this.decorators){
            this.decorators = []
        }else{
            if (this.containsDecorator(decorator)) {
                throw new Error(`C3Base already contains decorator for name ${decorator.type}`);
            }
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

    /**
     * Gets the fully qualified name for this {@link BaseComplexC3Type} which is the namespace + '.' + name
     * @return the fully qualified name for this {@link BaseComplexC3Type}
     */
    getQualifiedName(): string {
        return (this.namespace ? this.namespace + '.' : '') + this.name
    }

}

