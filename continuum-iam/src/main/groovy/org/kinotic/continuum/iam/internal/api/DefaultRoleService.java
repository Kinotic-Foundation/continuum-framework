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

import org.hibernate.Hibernate;
import org.kinotic.continuum.iam.api.RoleService;
import org.kinotic.continuum.iam.api.domain.Role;
import org.kinotic.continuum.iam.internal.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

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
    public CompletableFuture<Role> save(Role entity) {
        return CompletableFuture.supplyAsync(() -> roleRepository.save(entity));
    }

    @Override
    public CompletableFuture<Role> findById(String identity) {
        return CompletableFuture.supplyAsync(() -> transactionTemplate.execute(status -> {
            Optional<Role> value = roleRepository.findById(identity);
            // force lazy loaded data, so it will be available for the UI
            value.ifPresent(role -> Hibernate.initialize(role.getAccessPolicies()));
            return value.orElse(null);
        }));
    }

    @Override
    public CompletableFuture<Long> count() {
        return CompletableFuture.supplyAsync(roleRepository::count);
    }

    @Override
    public CompletableFuture<Void> deleteById(String identity) {
        return CompletableFuture.runAsync(() -> roleRepository.deleteById(identity));
    }

    @Override
    public CompletableFuture<Page<Role>> findAll(Pageable page) {
        return CompletableFuture.supplyAsync(() -> roleRepository.findAll(page));
    }

    public CompletableFuture<Page<Role>> findByIdNotIn(Collection<String> collection, Pageable page) {
        return CompletableFuture.supplyAsync(() -> {
            Page<Role> ret;
            if (collection != null && collection.size() > 0) {
                ret = roleRepository.findByIdNotIn(collection, page);
            } else {
                ret = findAll(page).join();
            }
            return ret;
        });
    }

    @Override
    public CompletableFuture<Page<Role>> search(String searchText, Pageable pageable) {
        return CompletableFuture.supplyAsync(() -> roleRepository.findLikeId(searchText, pageable));
    }
}
