import {C3Type} from "@/api/C3Type";

/**
 * Arrays are used for ordered elements.
 * Created by navid on 2023-4-13.
 */
export class ArrayC3Type extends C3Type {
    /**
     * The type the defined array will contain
     * <p>
     */
    // @ts-ignore
    private contains: C3Type | null = null;
}