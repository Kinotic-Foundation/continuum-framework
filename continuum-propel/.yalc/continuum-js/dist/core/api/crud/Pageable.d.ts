import { Sort } from './Sort';
/**
 * Abstract interface for pagination information.
 *
 * Adapted from the Spring Data Commons Package
 *
 * @author Oliver Gierke
 * @author Navid Mitchell
 */
export declare class Pageable {
    /**
     * Returns the page to be returned.
     */
    pageNumber: number;
    /**
     * Returns the number of items to be returned.
     */
    pageSize: number;
    /**
     * Returns the sorting parameters.
     */
    sort: Sort | null;
}
