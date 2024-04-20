import {AbstractDefinition} from '@/api/AbstractDefinition'
import {C3Type} from '@/api/C3Type'
import {C3Decorator} from '@/api/decorators/C3Decorator'

/**
 * Provides functionality to define an argument for a {@link FunctionDefinition}.
 */
export class ArgumentDefinition extends AbstractDefinition {
    /**
     * The name of this {@link ArgumentDefinition}
     */
    public name: string

    /**
     * The type of this {@link ArgumentDefinition}
     */
    public type: C3Type

    public constructor(name: string,
                       type: C3Type,
                       decorators?: C3Decorator[],
                       metadata?: { [p: string]: any } | null) {
        super(decorators, metadata)
        this.name = name
        this.type = type
    }
}
