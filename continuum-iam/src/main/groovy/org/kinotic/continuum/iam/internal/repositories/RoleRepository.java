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

package org.kinotic.continuum.iam.internal.repositories;

import org.kinotic.continuum.iam.api.domain.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

/**
 *
 * Created by navid on 2/3/20
 */
@Repository
public interface RoleRepository extends PagingAndSortingRepository<Role, String> {

    Page<Role> findByIdentityNotIn(Collection<String> ids, Pageable page);

    @Query("select r from Role r where r.identity LIKE CONCAT('%',?1,'%')")
    Page<Role> findLikeIdentity(String identity, Pageable page);

}
