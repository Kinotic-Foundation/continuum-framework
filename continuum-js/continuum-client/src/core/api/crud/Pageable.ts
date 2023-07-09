/*
 * Copyright 2008-2019 the original author or authors.
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

import {Sort} from './Sort'

/**
 * Abstract interface for pagination information.
 *
 * Adapted from the Spring Data Commons Package
 *
 * @author Oliver Gierke
 * @author Navid Mitchell
 */
export class Pageable {

    /**
     * Returns the page to be returned.
     */
    public pageNumber: number = 0

    /**
     * Returns the number of items to be returned.
     */
    public pageSize: number = 10

    /**
     * Returns the sorting parameters.
     */
    public sort?: Sort | null = null


    constructor(pageNumber: number, pageSize: number, sort?: Sort | null) {
        this.pageNumber = pageNumber
        this.pageSize = pageSize
        this.sort = sort
    }

    public static create(pageNumber: number, pageSize: number, sort?: Sort): Pageable {
        return new Pageable(pageNumber, pageSize, sort)
    }

}
