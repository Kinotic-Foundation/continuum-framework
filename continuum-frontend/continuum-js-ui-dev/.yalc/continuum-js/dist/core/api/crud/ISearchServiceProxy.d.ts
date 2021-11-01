import { Identifiable } from './Identifiable';
import { Page } from './Page';
import { Pageable } from './Pageable';
import { IDataSource } from "./IDataSource";
export interface ISearchServiceProxy<T extends Identifiable<string>> extends IDataSource<T> {
    /**
     * Returns a {@link Page} of entities meeting the paging restriction provided in the {@code Pageable} object.
     *
     * @param pageable the page settings to be used
     * @return a {@link Promise} emitting the page of entities
     */
    findAll(pageable: Pageable): Promise<Page<T>>;
    /**
     * Returns a {@link Page} of entities matching the search text and paging restriction provided in the {@code Pageable} object.
     *
     * @param searchText the text to search for entities for
     * @param pageable the page settings to be used
     * @return a {@link Promise} emitting the page of entities
     */
    search(searchText: string, pageable: Pageable): Promise<Page<T>>;
}
