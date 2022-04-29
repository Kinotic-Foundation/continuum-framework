/*
 *
 * Copyright 2008-2021 Kinotic and the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import {Identifiable} from './Identifiable'
import {Page} from './Page'
import {Pageable} from './Pageable'
import {IDataSource} from "./IDataSource";

export interface ISearchServiceProxy<T extends Identifiable<string>> extends IDataSource<T>{

    /**
     * Returns a {@link Page} of entities meeting the paging restriction provided in the {@code Pageable} object.
     *
     * @param pageable the page settings to be used
     * @return a {@link Promise} emitting the page of entities
     */
    findAll(pageable: Pageable): Promise<Page<T>>

    /**
     * Returns a {@link Page} of entities matching the search text and paging restriction provided in the {@code Pageable} object.
     *
     * @param searchText the text to search for entities for
     * @param pageable the page settings to be used
     * @return a {@link Promise} emitting the page of entities
     */
    search(searchText: string, pageable: Pageable): Promise<Page<T>>

}
