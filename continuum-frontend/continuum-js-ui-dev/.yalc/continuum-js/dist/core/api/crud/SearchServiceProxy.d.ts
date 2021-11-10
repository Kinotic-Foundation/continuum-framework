import { ISearchServiceProxy } from './ISearchServiceProxy';
import { IServiceProxy } from '../IServiceRegistry';
import { Identifiable } from './Identifiable';
import { Page } from './Page';
import { Pageable } from './Pageable';
export declare class SearchServiceProxy<T extends Identifiable<string>> implements ISearchServiceProxy<T> {
    protected serviceProxy: IServiceProxy;
    constructor(serviceProxy: IServiceProxy);
    findAll(pageable: Pageable): Promise<Page<T>>;
    search(searchText: string, pageable: Pageable): Promise<Page<T>>;
}
