import { IEvent, IEventBus } from './IEventBus';
import { Observable } from 'rxjs';
import { Optional } from 'typescript-optional';
/**
 * Default IEvent implementation
 */
export declare class Event implements IEvent {
    cri: string;
    headers: Map<string, string>;
    data: Optional<Uint8Array>;
    constructor(cri: string, headers?: Map<string, string>, data?: Uint8Array);
    getHeader(key: string): string | undefined;
    hasHeader(key: string): boolean;
    setHeader(key: string, value: string): void;
    removeHeader(key: string): boolean;
    setDataString(data: string): void;
    getDataString(): string;
}
export declare class EventBus implements IEventBus {
    private stompClient;
    private connected;
    private encodedIdentity;
    private replyToCri;
    private requestRepliesObservable;
    private requestRepliesSubscription;
    constructor();
    connect(url: string, identity: string, secret: string): Promise<void>;
    disconnect(): void;
    send(event: IEvent): void;
    request(event: IEvent): Promise<IEvent>;
    requestStream(event: IEvent, sendControlEvents?: boolean): Observable<IEvent>;
    observe(cri: string): Observable<IEvent>;
    /**
     * This is internal impl of observe that creates a cold observable.
     * The public variants transform this to some type of hot observable depending on the need
     * @param cri to observe
     * @return the cold {@link Observable<IEvent>} for the given destination
     */
    private _observe;
}
