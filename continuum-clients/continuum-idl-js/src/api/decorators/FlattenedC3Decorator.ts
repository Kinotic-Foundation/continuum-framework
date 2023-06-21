import {C3Decorator} from "@/api/decorators/C3Decorator"
import {DecoratorTarget} from "@/api/decorators/DecoratorTarget"

/**
 * Represents a {@link C3Decorator} the signifies that the decorated value must not be null
 * Created by Navíd Mitchell 🤪 on 4/23/23.
 */
export class FlattenedC3Decorator extends C3Decorator {

    constructor() {
        super()
        this.type = "Flattened"
        this.targets = [DecoratorTarget.FIELD]
    }
}