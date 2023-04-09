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

package org.kinotic.continuum.iam.internal.api;

import org.kinotic.continuum.iam.api.RoleService;
import org.kinotic.continuum.iam.api.domain.Role;
import org.kinotic.continuum.iam.internal.repositories.RoleRepository;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.Optional;

/**
 *
 * Created by navid on 2/3/20
 */
@Service
public class DefaultRoleService implements RoleService {

    @Autowired
    private RoleRepository roleRepository;

    private final TransactionTemplate transactionTemplate;


    public DefaultRoleService(PlatformTransactionManager transactionManager) {
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    @Override
    public Mono<Role> save(Role entity) {
        return Mono.fromSupplier(() -> roleRepository.save(entity));
    }

    @Override
    public Mono<Role> findById(String identity) {
        return Mono.fromSupplier(() -> transactionTemplate.execute(status -> {
            Optional<Role> value = roleRepository.findById(identity);
            // force lazy loaded data, so it will be available for the UI
            value.ifPresent(role -> Hibernate.initialize(role.getAccessPolicies()));
            return value.orElse(null);
        }));
    }

    @Override
    public Mono<Long> count() {
        return Mono.fromSupplier(roleRepository::count);
    }

    @Override
    public Mono<Void> deleteById(String identity) {
        return Mono.fromRunnable(() -> roleRepository.deleteById(identity));
    }

    @Override
    public Page<Role> findAll(Pageable page) {
        return roleRepository.findAll(page);
    }

    @Override
    public Page<Role> findByIdNotIn(Collection<String> collection, Pageable page) {
        Page<Role> ret;
        if(collection != null && collection.size() > 0){
            ret = roleRepository.findByIdNotIn(collection, page);
        }else{
            ret = findAll(page);
        }
        return ret;
    }

    @Override
    public Page<Role> search(String searchText, Pageable pageable) {
        return roleRepository.findLikeId(searchText, pageable);
    }

}
