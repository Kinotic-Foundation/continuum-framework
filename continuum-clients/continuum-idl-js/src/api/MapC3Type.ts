import { C3Type } from '@/api/C3Type';

/**
 * Defines a map of key value pairs
 * Created by navid on 2023-4-13.
 */
export class MapC3Type extends C3Type {
    /**
     * The type of the defined map's keys.
     */
    // @ts-ignore
    private key: C3Type;

    /**
     * The type of the defined map's values.
     */
    // @ts-ignore
    private value: C3Type;

}