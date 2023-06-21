import {C3Decorator} from "@/api/decorators/C3Decorator";
import {DecoratorTarget} from "@/api/decorators/DecoratorTarget";
import {MultiTenancyType} from "@/api/decorators/MultiTenancyType";


export class EntityDecorator extends C3Decorator {
    public multiTenancyType: MultiTenancyType = MultiTenancyType.NONE;
    constructor() {
        super()
        this.type = "Entity"
        this.targets = [DecoratorTarget.TYPE]
    }

    public withMultiTenancyType(type: MultiTenancyType): EntityDecorator {
        this.multiTenancyType = type
        return this
    }
}