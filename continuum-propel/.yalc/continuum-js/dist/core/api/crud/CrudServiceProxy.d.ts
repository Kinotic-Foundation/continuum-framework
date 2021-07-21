import { ICrudServiceProxy } from './ICrudServiceProxy';
import { IServiceProxy } from '../IServiceRegistry';
import { Identifiable } from './Identifiable';
import { Page } from './Page';
import { Pageable } from './Pageable';
export declare class CrudServiceProxy<T extends Identifiable<string>> implements ICrudServiceProxy<T> {
    protected serviceProxy: IServiceProxy;
    constructor(serviceProxy: IServiceProxy);
    count(): Promise<number>;
    create(entity: T): Promise<T>;
    deleteByIdentity(identity: string): Promise<void>;
    findAll(pageable: Pageable): Promise<Page<T>>;
    findByIdentity(identity: string): Promise<T>;
    save(entity: T): Promise<T>;
    findByIdNotIn(ids: string[], page: Pageable): Promise<Page<Identifiable<string>>>;
    search(searchText: string, pageable: Pageable): Promise<Page<T>>;
}
