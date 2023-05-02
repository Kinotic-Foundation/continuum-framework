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

import org.apache.commons.lang3.Validate;
import org.hibernate.Hibernate;
import org.kinotic.continuum.core.api.crud.IdentifiableCrudService;
import org.kinotic.continuum.iam.api.domain.Authenticator;
import org.kinotic.continuum.iam.api.domain.IamParticipant;
import org.kinotic.continuum.iam.internal.repositories.IamParticipantRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 *
 * Created by Navid Mitchell on 3/3/20
 */
public abstract class AbstractIamParticipantService implements IdentifiableCrudService<IamParticipant, String> {

    protected final IamParticipantRepository iamParticipantRepository;

    protected final TransactionTemplate transactionTemplate;

    public AbstractIamParticipantService(IamParticipantRepository iamParticipantRepository,
                                         PlatformTransactionManager transactionManager) {

        this.iamParticipantRepository = iamParticipantRepository;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    protected abstract Map.Entry<String, String> getTypeMetadata();

    @Override
    public CompletableFuture<IamParticipant> create(IamParticipant entity) {
        Validate.notNull(entity);
        return findById(entity.getId())
                .thenCompose(result -> {
                    if (result == null) {
                        entity.putMetadata(getTypeMetadata());
                        return save(entity);
                    } else {
                        CompletableFuture<IamParticipant> exceptionFuture = new CompletableFuture<>();
                        exceptionFuture.completeExceptionally(new IllegalArgumentException(entity.getClass().getSimpleName() + " for the identity " + entity.getId() + " already exists"));
                        return exceptionFuture;
                    }
                });
    }

    @Override
    public CompletableFuture<IamParticipant> save(IamParticipant entity) {
        Validate.notNull(entity);
        return CompletableFuture.supplyAsync(() -> transactionTemplate.execute(status -> {
            if (entity.getAuthenticators() == null) {
                Optional<IamParticipant> value = iamParticipantRepository.findById(entity.getId());
                value.ifPresent(iamParticipant -> entity.setAuthenticators(iamParticipant.getAuthenticators()));
            } else {
                for (Authenticator authProvider : entity.getAuthenticators()) {
                    authProvider.setIamParticipant(entity);
                }
            }
            return iamParticipantRepository.save(entity);
        }));
    }

    @Override
    public CompletableFuture<IamParticipant> findById(String identity) {
        Validate.notEmpty(identity);
        return CompletableFuture.supplyAsync(() -> transactionTemplate.execute(status -> {
            Optional<IamParticipant> value = iamParticipantRepository.findById(identity);
            value.ifPresent(iamParticipant -> {
                Hibernate.initialize(iamParticipant.getRoles());
                Hibernate.initialize(iamParticipant.getAuthenticators());
            });
            return value.orElse(null);
        }));
    }

    @Override
    public CompletableFuture<Long> count() {
        return CompletableFuture.supplyAsync(iamParticipantRepository::count);
    }

    @Override
    public CompletableFuture<Void> deleteById(String identity) {
        return CompletableFuture.runAsync(() -> iamParticipantRepository.deleteById(identity));
    }

    @Override
    public CompletableFuture<Page<IamParticipant>> findAll(Pageable page) {
        Map.Entry<String, String> type = getTypeMetadata();
        return CompletableFuture.supplyAsync(() -> iamParticipantRepository.findByMetadataAndValue(type.getKey(), type.getValue(), page));
    }

    public CompletableFuture<Page<IamParticipant>> findByIdNotIn(Collection<String> collection, Pageable page) {
        return CompletableFuture.supplyAsync(() -> iamParticipantRepository.findByIdNotIn(collection, page));
    }

    @Override
    public CompletableFuture<Page<IamParticipant>> search(String searchText, Pageable pageable) {
        Map.Entry<String, String> type = getTypeMetadata();
        return CompletableFuture.supplyAsync(() -> iamParticipantRepository.findLikeIdByMetadataAndValue(searchText, type.getKey(), type.getValue(), pageable));
    }
}
