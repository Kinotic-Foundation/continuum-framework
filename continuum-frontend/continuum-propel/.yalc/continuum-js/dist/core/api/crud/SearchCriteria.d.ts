export declare enum SearchComparator {
    EQUALS = "=",
    NOT = "!",
    GREATER_THAN = ">",
    GREATER_THAN_OR_EQUALS = ">=",
    LESS_THAN = "<",
    LESS_THAN_OR_EQUALS = "<=",
    LIKE = "~"
}
export declare class SearchCriteria<T> {
    key: string;
    value: T;
    searchComparator: SearchComparator;
    constructor(key: string, value: T, searchComparator: SearchComparator);
}
