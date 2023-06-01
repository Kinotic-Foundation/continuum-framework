

export enum DecoratorTarget {
    /** Class, interface, or enum declaration */
    TYPE = "TYPE",
    /** Field declaration (includes enum constants) */
    FIELD = "FIELD",
    /** Function declaration */
    FUNCTION = "FUNCTION",
    /** Formal parameter declaration */
    PARAMETER = "PARAMETER",
    /** Constructor declaration */
    CONSTRUCTOR = "CONSTRUCTOR",
    /** Namespace declaration */
    NAMESPACE = "NAMESPACE"
}