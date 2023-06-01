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
    public metadata: Map<string, any> = new Map<string, any>()
    /**
     * The list of Decorators that should be applied to this type
     */
    public decorators?: C3Decorator[]

    public type: string = ""

    public containsDecorator(clazz: typeof C3Decorator): boolean {
        return this.findDecorator(clazz) !== null
    }

    /**
     * Checks if this type has any {@link C3Decorator}
     * @return true if any {@link C3Decorator}s are present false if not
     */
    public hasDecorators(): boolean {
        return this.decorators != null && this.decorators.length > 0
    }

    public findDecorator(clazz: typeof C3Decorator): C3Decorator | null {
        let ret: C3Decorator | null = null
        if (this.decorators) {
            for (const decorator of this.decorators) {
                // FIXME: maybe this works? need to test it out.
                if(Object.getPrototypeOf(clazz) === Object.getPrototypeOf(decorator)){
                    ret = decorator
                    break
                }
            }
        }
        return ret
    }
}