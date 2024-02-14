import { C3Type } from '@/api/C3Type'

/**
 * Created by Navíd Mitchell 🤪 on 4/13/23.
 */
export class EnumC3Type extends C3Type {
    /**
     * The namespace that this {@link EnumC3Type} belongs to
     */
    public namespace!: string

    /**
     * This is the name of the {@link EnumC3Type} such as "EventType"
     */
    public name!: string

    /**
     * The values that are part of this enum
     */
    public values: string[] = []

    constructor() {
        super();
        this.type = "enum"
    }

    public addValue(value: string): EnumC3Type {
        this.values.push(value)
        return this
    }
}
