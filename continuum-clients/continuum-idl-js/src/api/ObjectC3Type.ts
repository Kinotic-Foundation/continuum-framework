import { C3Type } from '@/api/C3Type'

/**
 * Objects map property names to values. The property names are strings, and the values can be any Continuum Schema type.
 * Each of these pairs is conventionally referred to as a “property”.
 * <p>
 * Created by navid on 2019-06-11.
 */
export class ObjectC3Type extends C3Type {

    /**
     * The namespace that this {@link ObjectC3Type} belongs to
     */
    public namespace: string = ''

    /**
     * This is the name of the {@link ObjectC3Type} such as "Person", "Animal"
     */
    public name: string =  ''

    /**
     * The parent schema of this object definition.
     * This is used to support inheritance.
     */
    public parent: ObjectC3Type | null = null

    /**
     * The properties (key-value pairs) on an object are defined using the properties' keyword.
     * The value of properties is an object, where each key is the name of a property and each value is a Continuum schema used to validate that property.
     */
    public properties: {[key: string]: C3Type} = {}

    constructor() {
        super();
        this.type = "object"
    }

    public addProperty(name: string, c3Type: C3Type): ObjectC3Type {
        if (this.properties.hasOwnProperty(name)) {
            throw new Error(`ObjectTypeDefinition already contains property for name ${name}`);
        }

        this.properties[name] = c3Type
        return this
    }

    /**
     * Gets the URN for this {@link ObjectC3Type} which is the namespace + "." + name
     * @return the urn for this {@link ObjectC3Type}
     */
    public getUrn(): string {
        return this.namespace + "." + this.name
    }

}
