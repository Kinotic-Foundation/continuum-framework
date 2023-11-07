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

/**
 * A page is a sublist of a list of objects.
 * @author Navid Mitchell
 */
export class Page<T> {

    /**
     * Returns the total amount of elements or null or undefined if not known.
     */
    readonly totalElements: number | null | undefined = undefined

    /**
     * The cursor to be used for subsequent retrieval of data, or null if this is the last page, or undefined if cursor paging is not being used.
     */
    readonly cursor: string | null | undefined = undefined

    /**
     * Returns the page content as {@link Array}.
     */
    readonly content: T[] = []

}
