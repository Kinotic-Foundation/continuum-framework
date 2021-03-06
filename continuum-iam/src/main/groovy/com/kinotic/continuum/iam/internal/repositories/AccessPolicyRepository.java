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

package com.kinotic.continuum.iam.internal.repositories;

import com.kinotic.continuum.iam.api.domain.AccessPolicy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Collection;

/**
 *
 * Created by navid on 2/3/20
 */
public interface AccessPolicyRepository extends PagingAndSortingRepository<AccessPolicy, String> {

    Page<AccessPolicy> findByIdentityNotIn(Collection<String> ids, Pageable page);

    @Query("select a from AccessPolicy a where a.identity LIKE CONCAT('%',?1,'%')")
    Page<AccessPolicy> findLikeIdentity(String identity, Pageable page);

}
