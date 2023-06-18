import {C3Decorator} from "@/api/decorators/C3Decorator";
import {DecoratorTarget} from "@/api/decorators/DecoratorTarget";


export class IdDecorator extends C3Decorator {

    constructor() {
        super()
        this.type = "Id"
        this.targets = [DecoratorTarget.FIELD]
    }
}