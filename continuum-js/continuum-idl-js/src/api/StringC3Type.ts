import {BaseC3Type} from '@/api/BaseC3Type'

/**
 * The string type is used for strings of text. It may contain Unicode characters.
 * Created by navid on 2023-4-13.
 */
export class StringC3Type extends BaseC3Type {

    constructor() {
        super('string')
    }
}
