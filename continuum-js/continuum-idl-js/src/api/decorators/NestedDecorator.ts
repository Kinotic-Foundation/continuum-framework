import {C3Decorator} from '@/api/decorators/C3Decorator'

/**
 * Marks a property as a nested object.
 * Created by Navíd Mitchell 🤪 on 4/23/23.
 */
export class NestedDecorator extends C3Decorator {

    constructor() {
        super()
        this.type = 'Nested'
    }
}
