import {C3Decorator} from "@/api/decorators/C3Decorator"

/**
 * Represents a {@link C3Decorator} the signifies that the decorated value must not be null
 * Created by Navíd Mitchell 🤪 on 4/23/23.
 */
export class NotNullC3Decorator extends C3Decorator {

    constructor() {
        super()
        this.type = "NotNull"
    }
}