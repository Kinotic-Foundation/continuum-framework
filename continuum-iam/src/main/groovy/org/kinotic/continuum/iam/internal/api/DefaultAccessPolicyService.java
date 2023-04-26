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
import org.kinotic.continuum.iam.api.AccessPolicyService;
import org.kinotic.continuum.iam.api.domain.AccessPolicy;
import org.kinotic.continuum.iam.internal.repositories.AccessPolicyRepository;
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
public class DefaultAccessPolicyService implements AccessPolicyService {

    private AccessPolicyRepository accessPolicyRepository;
    private final TransactionTemplate transactionTemplate;

    public DefaultAccessPolicyService(AccessPolicyRepository accessPolicyRepository,
                                      PlatformTransactionManager transactionManager) {
        this.accessPolicyRepository = accessPolicyRepository;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    @Override
    public CompletableFuture<AccessPolicy> save(AccessPolicy entity) {
        return CompletableFuture.supplyAsync(() -> accessPolicyRepository.save(entity));
    }

    @Override
    public CompletableFuture<AccessPolicy> findById(String identity) {
        return CompletableFuture.supplyAsync(() -> transactionTemplate.execute(status -> {
            Optional<AccessPolicy> value = accessPolicyRepository.findById(identity);
            value.ifPresent(policy -> {
                Hibernate.initialize(policy.getAllowedSendPatterns());
                Hibernate.initialize(policy.getAllowedSubscriptionPatterns());
            });
            return value.orElse(null);
        }));
    }

    @Override
    public CompletableFuture<Long> count() {
        return CompletableFuture.supplyAsync(accessPolicyRepository::count);
    }

    @Override
    public CompletableFuture<Void> deleteById(String identity) {
        return CompletableFuture.runAsync(() -> accessPolicyRepository.deleteById(identity));
    }

    @Override
    public CompletableFuture<Page<AccessPolicy>> findAll(Pageable page) {
        return CompletableFuture.supplyAsync(() -> accessPolicyRepository.findAll(page));
    }

    @Override
    public CompletableFuture<Page<AccessPolicy>> findByIdNotIn(Collection<String> collection, Pageable page) {
        return CompletableFuture.supplyAsync(() -> {
            Page<AccessPolicy> ret;
            if (collection != null && collection.size() > 0) {
                ret = accessPolicyRepository.findByIdNotIn(collection, page);
            } else {
                ret = accessPolicyRepository.findAll(page);
            }
            return ret;
        });
    }

    @Override
    public CompletableFuture<Page<AccessPolicy>> search(String searchText, Pageable pageable) {
        return CompletableFuture.supplyAsync(() -> accessPolicyRepository.findLikeId(searchText, pageable));
    }
}
