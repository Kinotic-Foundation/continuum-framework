import { C3Type } from '@/api/C3Type'

/**
 * A simple schema to allow referencing other components in the specification, internally and externally.
 * Created by navid on 2023-4-13.
 */
export class ReferenceC3Type extends C3Type {
    /**
     * The urn to the schema being referenced.
     */
    public urn: string | null = null

}
