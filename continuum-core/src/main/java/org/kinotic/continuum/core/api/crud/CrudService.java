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

package org.kinotic.continuum.core.api.crud;

import org.kinotic.continuum.api.Identifiable;
import org.kinotic.continuum.internal.utils.ReactorUtil;
import org.apache.commons.lang3.Validate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Mono;

import java.util.Collection;

/**
 * Basic CRUD operations interface this is provided so clients of the API can have a general contract for all domain objects
 *
 * Created by navid on 2/3/20
 */
public interface CrudService<T extends Identifiable<String>> {

    /**
     * Creates a new entity if one does not already exist for the given id
     * @param entity to create if one does not already exist
     * @return a {@link Mono} containing the new entity or an error if an exception occurred
     */
    default Mono<T> create(T entity){
        Validate.notNull(entity);
        return Mono.create(sink -> findById(entity.getId())
            .doOnSuccess(result -> {
                if(result == null){
                    save(entity).subscribe(ReactorUtil.monoSinkToSubscriber(sink));
                }else{
                    sink.error(new IllegalArgumentException(entity.getClass().getSimpleName() + " for the identity " + entity.getId() + " already exists"));
                }
            })
            .subscribe(v -> {}, sink::error)); // We use an empty consumer this is handled with doOnSuccess, this is done so we get a single "signal" instead of onNext, onComplete type logic..
    }

    /**
     * Saves a given entity. Use the returned instance for further operations as the save operation might have changed the
     * entity instance completely.
     *
     * @param entity must not be {@literal null}.
     * @return {@link Mono} emitting the saved entity.
     * @throws IllegalArgumentException in case the given {@literal entity} is {@literal null}.
     */
    Mono<T> save(T entity);

    /**
     * Retrieves an entity by its identity.
     *
     * @param id must not be {@literal null}.
     * @return {@link Mono} emitting the entity with the given id or {@link Mono#empty()} if none found.
     * @throws IllegalArgumentException in case the given {@literal identity} is {@literal null}.
     */
    Mono<T> findById(String id);

    /**
     * Returns the number of entities available.
     *
     * @return {@link Mono} emitting the number of entities.
     */
    Mono<Long> count();

    /**
     * Deletes the entity with the given identity.
     *
     * @param id must not be {@literal null}.
     * @return {@link Mono} signaling when operation has completed.
     * @throws IllegalArgumentException in case the given {@literal identity} is {@literal null}.
     */
    Mono<Void> deleteById(String id);

    /**
     * Returns a {@link Page} of entities meeting the paging restriction provided in the {@code Pageable} object.
     *
     * @param pageable the page settings to be used
     * @return a page of entities
     */
    Page<T> findAll(Pageable pageable);


    /**
     * Returns a {@link Page} of entities not in the ids list and meeting the paging restriction provided in the {@link Pageable} object.
     * @param collection object that holds the collection data
     * @param pageable the page settings to be used by findAll
     * @return a page of entities
     */
    Page<T> findByIdNotIn(Collection<String> collection, Pageable pageable);


    /**
     * Returns a {@link Page} of entities matching the search text and paging restriction provided in the {@code Pageable} object.
     *
     * @param searchText the text to search for entities for
     * @param pageable the page settings to be used
     * @return a page of entities
     */
    Page<T> search(String searchText, Pageable pageable);

}
