import {C3Type} from "@/api/C3Type";
import {ObjectC3Type} from "@/api/ObjectC3Type";

/**
 * Represents a union type in the IDL.
 * Union types are a way to represent a type that can be one of many types.
 * Created by Navíd Mitchell 🤪 on 4/13/23.
 */
export class UnionC3Type extends C3Type {
    /**
     * This is the field that will be used to determine which type is being used.
     * The field must exist on all types in this union.
     */
    // @ts-ignore
    private discriminator: string;
    /**
     * The types that are part of this union
     * All types must have a field with the name of the discriminator
     */
    // @ts-ignore
    private types: ObjectC3Type[] = [];

}