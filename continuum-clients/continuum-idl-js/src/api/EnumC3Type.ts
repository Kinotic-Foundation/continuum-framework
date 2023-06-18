import { C3Type } from '@/api/C3Type'

/**
 * Created by Navíd Mitchell 🤪 on 4/13/23.
 */
export class EnumC3Type extends C3Type {
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