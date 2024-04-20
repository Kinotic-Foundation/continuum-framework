import {BaseComplexC3Type} from '@/api/BaseComplexC3Type'
import {C3Decorator} from '@/api/decorators/C3Decorator'
import {MetadataType} from '@/api/HasMetadata'

/**
 * Created by Navíd Mitchell 🤪 on 4/13/23.
 */
export class EnumC3Type extends BaseComplexC3Type {

    /**
     * The values that are part of this enum
     */
    public values: string[] = []

    constructor(namespace: string | null,
                name: string,
                decorators?: C3Decorator[] | null,
                metadata?: MetadataType | null) {
        super('enum', namespace, name, decorators, metadata)
    }

    public addValue(value: string): EnumC3Type {
        this.values.push(value)
        return this
    }
}
