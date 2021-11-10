/**
 * A page is a sublist of a list of objects. It allows gain information about the position of it in the containing
 * entire list.
 *
 * Adapted from the Spring Data Commons Package
 *
 * @param <T>
 * @author Oliver Gierke
 * @author Navid Mitchell
 */
export declare class Page<T> {
    /**
     * Returns the size of the {@link Page}.
     */
    readonly size: number;
    /**
     * Returns the total amount of elements.
     */
    readonly totalElements: number;
    /**
     * Returns the page content as {@link Array}.
     */
    readonly content: T[];
}
