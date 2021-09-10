import { ICrudServiceProxy } from './ICrudServiceProxy';
import { Identifiable } from './Identifiable';
import { ISearchServiceProxy } from "./ISearchServiceProxy";
/**
 * Produces {@link ICrudServiceProxy} Proxies for a known remote CRUD service
 */
export interface ICrudServiceProxyFactory {
    crudServiceProxy<T extends Identifiable<string>>(serviceIdentifier: string): ICrudServiceProxy<T>;
    searchServiceProxy<T extends Identifiable<string>>(serviceIdentifier: string): ISearchServiceProxy<T>;
}
