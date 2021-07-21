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

package com.kinotic.continuum.iam.internal.api;

import com.kinotic.continuum.core.api.crud.CrudService;
import com.kinotic.continuum.iam.api.domain.Authenticator;
import com.kinotic.continuum.iam.api.domain.IamParticipant;
import com.kinotic.continuum.iam.internal.repositories.IamParticipantRepository;
import com.kinotic.continuum.internal.utils.ReactorUtils;
import org.apache.commons.lang3.Validate;
import org.hibernate.Hibernate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

/**
 *
 * Created by Navid Mitchell on 3/3/20
 */
public abstract class AbstractIamParticipantService implements CrudService<IamParticipant> {

    protected final IamParticipantRepository iamParticipantRepository;

    protected final TransactionTemplate transactionTemplate;

    public AbstractIamParticipantService(IamParticipantRepository iamParticipantRepository,
                                         PlatformTransactionManager transactionManager) {

        this.iamParticipantRepository = iamParticipantRepository;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }


    /**
     * Abstract method to get type metadata applied to all {@link IamParticipant}'s created or fetched
     * @return a {@link Map.Entry} containing the metadata that defines the "concrete" type of this {@link IamParticipant}
     */
    protected abstract Map.Entry<String, String> getTypeMetadata();

    @Override
    public Mono<IamParticipant> create(IamParticipant entity) {
        Validate.notNull(entity);
        return Mono.create(sink -> findByIdentity(entity.getIdentity())
                .doOnSuccess(result -> {
                    if(result == null){
                        entity.putMetadata(getTypeMetadata());
                        save(entity).subscribe(ReactorUtils.monoSinkToSubscriber(sink));
                    }else{
                        sink.error(new IllegalArgumentException(entity.getClass().getSimpleName() + " for the identity " + entity.getIdentity() + " already exists"));
                    }
                })
                .subscribe(v -> {}, sink::error)); // We use an empty consumer this is handled with doOnSuccess, this is done so we get a single "signal" instead of onNext, onComplete type logic..
    }

    @Override
    public Mono<IamParticipant> save(IamParticipant entity) {
        Validate.notNull(entity);
        return Mono.fromSupplier(() -> transactionTemplate.execute(status -> {
            // If this is null it is because no changes were made on the client
            // This was the best way I could figure out how to return the Auth information to the client
            // but also allow intuitive changes on the client side
            if (entity.getAuthenticators() == null) {
                Optional<IamParticipant> value = iamParticipantRepository.findById(entity.getIdentity());
                value.ifPresent(iamParticipant -> entity.setAuthenticators(iamParticipant.getAuthenticators()));
            } else {
                // make sure all mapping is bi directional
                for (Authenticator authProvider : entity.getAuthenticators()) {
                    authProvider.setIamParticipant(entity);
                }
            }
            return iamParticipantRepository.save(entity);
        }));
    }

    @Override
    public Mono<IamParticipant> findByIdentity(String identity) {
        Validate.notEmpty(identity);
        return Mono.fromSupplier(() -> transactionTemplate.execute(status -> {
            Optional<IamParticipant> value = iamParticipantRepository.findById(identity);
            // force lazy loaded data so it will be available for the UI
            value.ifPresent(iamParticipant -> {
                Hibernate.initialize(iamParticipant.getRoles());
                Hibernate.initialize(iamParticipant.getAuthenticators());
            });
            return value.orElse(null);
        }));
    }

    @Override
    public Mono<Long> count() {
        return Mono.fromSupplier(iamParticipantRepository::count);
    }

    @Override
    public Mono<Void> deleteByIdentity(String identity) {
        return Mono.fromRunnable(() -> iamParticipantRepository.deleteById(identity));
    }

    @Override
    public Page<IamParticipant> findAll(Pageable page) {
        Map.Entry<String, String> type = getTypeMetadata();
        return iamParticipantRepository.findByMetadataAndValue(type.getKey(), type.getValue(), page);
    }

    @Override
    public Page<IamParticipant> findByIdNotIn(Collection<String> collection, Pageable page) {
        return iamParticipantRepository.findByIdentityNotIn(collection, page);
    }

    @Override
    public Page<IamParticipant> search(String searchText, Pageable pageable) {
        Map.Entry<String, String> type = getTypeMetadata();
        return iamParticipantRepository.findLikeIdentityByMetadataAndValue(searchText, type.getKey(), type.getValue(), pageable);
    }
}
