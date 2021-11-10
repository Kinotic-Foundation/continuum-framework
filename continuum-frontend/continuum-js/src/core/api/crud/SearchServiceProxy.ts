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

import { ISearchServiceProxy } from './ISearchServiceProxy'
import { IServiceProxy } from '../IServiceRegistry'
import { Identifiable } from './Identifiable'
import { Page } from './Page'
import { Pageable } from './Pageable'
import { injectable } from 'inversify-props'

@injectable()
export class SearchServiceProxy<T extends Identifiable<string>> implements ISearchServiceProxy<T> {

    protected serviceProxy: IServiceProxy

    constructor(serviceProxy: IServiceProxy) {
        this.serviceProxy = serviceProxy
    }

    public findAll(pageable: Pageable): Promise<Page<T>> {
        return this.serviceProxy.invoke('findAll', [pageable])
    }

    public search(searchText: string, pageable: Pageable): Promise<Page<T>> {
        return this.serviceProxy.invoke('search', [searchText, pageable])
    }
}
