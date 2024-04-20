import {C3Type} from '@/api/C3Type'

/**
 * This is the base class for all type schemas in C3.
 * <p>
 * Created by navid on 2023-4-13.
 */
export class BaseC3Type implements C3Type {

    public readonly type: string = ''

    protected constructor(type: string) {
        this.type = type
    }

}
