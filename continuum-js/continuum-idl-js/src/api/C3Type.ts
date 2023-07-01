import {C3Decorator} from "@/api/decorators/C3Decorator"

/**
 * This is the base class for all type schemas.
 * It can be used to create {@link C3Type} from use within a Continuum IDL.
 * <p>
 * Created by navid on 2023-4-13.
 */
export abstract class C3Type {
    /**
     * The metadata keyword is legal on any schema, The objects provided must be serializable to JSON.
     * Usually, metadata is for putting things like descriptions or hints for code generators, or other things tools can use.
     */
    public metadata: {[key: string]: any} = {}
    /**
     * The list of Decorators that should be applied to this type
     */
    public decorators: C3Decorator[] = []

    public type: string = ""

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

    public addDecorator(decorator: C3Decorator): C3Type {
        if (this.containsDecorator(decorator)) {
            throw new Error(`C3Type already contains decorator for name ${decorator.type}`);
        }

        this.decorators.push(decorator)
        return this
    }

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
