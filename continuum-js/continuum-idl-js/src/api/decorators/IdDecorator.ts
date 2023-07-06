import {C3Decorator} from '@/api/decorators/C3Decorator'

/**
 * Signifies the ID field of an entity, The value must be a string and provided by the client.
 */
export class IdDecorator extends C3Decorator {

    constructor() {
        super()
        this.type = 'Id'
    }
}
