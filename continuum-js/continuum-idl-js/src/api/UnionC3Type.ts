import {C3Type} from "@/api/C3Type"
import {ObjectC3Type} from "@/api/ObjectC3Type"

/**
 * Represents a union type in the IDL.
 * Union types are a way to represent a type that can be one of many types.
 * Created by Navíd Mitchell 🤪 on 4/13/23.
 */
export class UnionC3Type extends C3Type {

    /**
     * The namespace that this {@link UnionC3Type} belongs to
     */
    public namespace!: string

    /**
     * This is the name of the {@link UnionC3Type} such as "Animal"
     */
    public name!: string

    /**
     * The types that are part of this union
     * All types must have a field with the name of the discriminator
     */
    public types: ObjectC3Type[] = []

    constructor() {
        super();
        this.type = "union"
    }
}
