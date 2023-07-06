import {C3Decorator} from '@/api/decorators/C3Decorator'

/**
 * Signals that a property is a text field, and will be full text indexed.
 * Created by Navíd Mitchell 🤪 on 4/23/23.
 */
export class TextDecorator extends C3Decorator {

    constructor() {
        super()
        this.type = 'Text'
    }
}
