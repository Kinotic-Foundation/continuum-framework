import {C3Type} from '@/api/C3Type'
import {HasDecorators} from '@/api/HasDecorators'
import {HasMetadata} from '@/api/HasMetadata'
import {HasQualifiedName} from '@/api/HasQualifiedName'

/**
 * The base interface for all complex types in the Continuum IDL
 * Created by navid on 2023-4-13.
 */
export interface ComplexC3Type extends C3Type, HasQualifiedName, HasDecorators, HasMetadata {

}
