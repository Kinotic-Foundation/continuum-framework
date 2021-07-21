import { Identifiable } from './crud/Identifiable';
export declare enum StreamOperation {
    EXISTING = "EXISTING",
    UPDATE = "UPDATE",
    REMOVE = "REMOVE"
}
/**
 * Holder for domain objects that will be returned as a stream of changes to a data set
 *
 * Created by Navid Mitchell on 6/3/20
 */
export declare class StreamData<I, T> implements Identifiable<I> {
    streamOperation: StreamOperation;
    identity: I;
    value: T;
    constructor(streamOperation: StreamOperation, identity: I, value: T);
    isSet(): boolean;
}
