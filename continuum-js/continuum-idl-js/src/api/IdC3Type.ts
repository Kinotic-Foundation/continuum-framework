import {C3Type} from "@/api/C3Type"
import {IdDecorator} from "@/api/decorators/IdDecorator";

/**
 * The id type is used for ids.
 * Created by navid on 2023-4-13.
 */
export class IdC3Type extends C3Type {

    constructor() {
        super();
        this.type = "string"
        this.addDecorator(new IdDecorator())
    }
}