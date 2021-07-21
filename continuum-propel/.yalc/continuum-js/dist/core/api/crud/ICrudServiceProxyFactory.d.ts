import { ICrudServiceProxy } from './ICrudServiceProxy';
import { Identifiable } from './Identifiable';
/**
 * Produces {@link ICrudServiceProxy} Proxies for a known remote CRUD service
 */
export interface ICrudServiceProxyFactory {
    crudServiceProxy<T extends Identifiable<string>>(serviceIdentifier: string): ICrudServiceProxy<T>;
}
