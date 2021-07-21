/**
 * Enumeration for sort directions.
 *
 * Adapted from the Spring Data Commons Package
 *
 * @author Oliver Gierke
 * @author Navid Mitchell
 */
export declare enum Direction {
    ASC = "ASC",
    DESC = "DESC"
}
/**
 * Enumeration for null handling hints that can be used in {@link Order} expressions.
 *
 * Adapted from the Spring Data Commons Package
 *
 * @author Thomas Darimont
 * @author Navid Mitchell
 * @since 1.8
 */
export declare enum NullHandling {
    /**
     * Lets the data store decide what to do with nulls.
     */
    NATIVE = "NATIVE",
    /**
     * A hint to the used data store to order entries with null values before non null entries.
     */
    NULLS_FIRST = "NULLS_FIRST",
    /**
     * A hint to the used data store to order entries with null values after non null entries.
     */
    NULLS_LAST = "NULLS_LAST"
}
export declare class Order {
    property: string;
    direction: Direction;
    nullHandling: NullHandling;
    constructor(property: string, direction: Direction | null);
    /**
     * Returns whether sorting for this property shall be ascending.
     */
    isAscending(): boolean;
    /**
     * Returns whether sorting for this property shall be descending.
     */
    isDescending(): boolean;
}
/**
 * Sort option for queries. You have to provide at least a list of properties to sort for that must not include
 * {@literal null} or empty strings. The direction defaults to {@link Sort#DEFAULT_DIRECTION}.
 *
 * Adapted from the Spring Data Commons Package
 *
 * @author Oliver Gierke
 * @author Thomas Darimont
 * @author Mark Paluch
 * @author Navid Mitchell
 */
export declare class Sort {
    orders: Order[];
}
