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
 * A page is a sublist of a list of objects. It allows gain information about the position of it in the containing
 * entire list.
 *
 * Adapted from the Spring Data Commons Package
 *
 * @param <T>
 * @author Oliver Gierke
 * @author Navid Mitchell
 */
export class Page<T> {

    /**
     * Returns the size of the {@link Page}.
     */
    public readonly size: number = 0

    /**
     * Returns the total amount of elements.
     */
    public readonly totalElements: number = 0

    /**
     * Returns the page content as {@link Array}.
     */
    public readonly content: T[] = []

}
