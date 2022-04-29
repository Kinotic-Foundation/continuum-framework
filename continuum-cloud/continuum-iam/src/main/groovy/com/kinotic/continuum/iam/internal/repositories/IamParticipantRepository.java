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

import com.kinotic.continuum.iam.api.domain.IamParticipant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

/**
 *
 * Created by Navid Mitchell on 2019-02-03.
 */
@Repository
public interface IamParticipantRepository extends PagingAndSortingRepository<IamParticipant,String> {

    @Query("select p from IamParticipant p where p.metadata[?1] = ?2")
    Page<IamParticipant> findByMetadataAndValue(String key, String value, Pageable page);

    @Query("select p from IamParticipant p where p.identity LIKE CONCAT('%',?1,'%') and p.metadata[?2] = ?3")
    Page<IamParticipant> findLikeIdentityByMetadataAndValue(String identity, String key, String value, Pageable page);

    Page<IamParticipant> findByIdentityNotIn(Collection<String> ids, Pageable page);

}
