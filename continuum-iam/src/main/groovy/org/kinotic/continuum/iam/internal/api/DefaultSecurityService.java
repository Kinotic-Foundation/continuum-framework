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

import org.kinotic.continuum.core.api.security.Participant;
import org.kinotic.continuum.core.api.security.Permissions;
import org.kinotic.continuum.core.api.security.SecurityService;
import org.kinotic.continuum.iam.api.domain.*;
import org.kinotic.continuum.iam.api.domain.*;
import org.kinotic.continuum.iam.internal.repositories.AuthenticatorRepository;
import org.kinotic.continuum.iam.internal.repositories.IamParticipantRepository;
import org.kinotic.continuum.internal.core.api.security.DefaultParticipant;
import org.kinotic.continuum.internal.utils.ContinuumUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import reactor.core.publisher.Mono;

import java.util.Optional;

/**
 *
 * Created by Navid Mitchell on 2019-02-03.
 */
@Service
public class DefaultSecurityService implements SecurityService {

    protected final AuthenticatorRepository authenticatorRepository;
    protected final IamParticipantRepository iamParticipantRepository;
    protected final TransactionTemplate transactionTemplate;

    public DefaultSecurityService(AuthenticatorRepository authenticatorRepository,
                                  IamParticipantRepository iamParticipantRepository,
                                  TransactionTemplate transactionTemplate) {
        this.authenticatorRepository = authenticatorRepository;
        this.iamParticipantRepository = iamParticipantRepository;
        this.transactionTemplate = transactionTemplate;
    }

    @Override
    public Mono<Participant> authenticate(String accessKey, String secretToken) {
        return Mono.create(sink -> transactionTemplate.executeWithoutResult(status -> {
            try {
                //  TODO: remove when devices are moved away from legacy format
                //  Legacy format for accessKey was UUID_seconds. In order to look up Authenticator we need just the uuid
                String key;
                String secret;
                if (accessKey.contains("_") && !accessKey.contains("@")) { // make sure this is not an email that has a _
                    String[] parts = accessKey.split("_");
                    if (parts.length != 2) {
                        throw new IllegalArgumentException("The accessKey format was unexpected");
                    }
                    key = parts[0];
                    secret = secretToken + "_" + parts[1];
                } else {
                    key = accessKey;
                    secret = secretToken;
                }

                Optional<Authenticator> authenticator = authenticatorRepository.findById(key);
                if (authenticator.isPresent()) {
                    if (authenticator.get().canAuthenticate(secret)) {
                        IamParticipant iamParticipant = authenticator.get().getIamParticipant();

                        DefaultParticipant participant = new DefaultParticipant(iamParticipant.getIdentity());
                        participant.setMetadata(iamParticipant.getMetadata());
                        participant.setPermissions(createPermissionsForParticipant(iamParticipant));

                        sink.success(participant);
                    } else {
                        sink.error(new IllegalArgumentException("Participant cannot be authenticated with information provided"));
                    }
                } else {
                    sink.error(new IllegalArgumentException("Participant cannot be authenticated with information provided"));
                }

            } catch (Exception e) {
                sink.error(e);
            }
        }));
    }

    @Override
    public Mono<Participant> findParticipant(String participantIdentity) {
        return Mono.create(sink -> transactionTemplate.executeWithoutResult(status -> {
            try {
                Optional<IamParticipant> iamParticipantOptional = iamParticipantRepository.findById(participantIdentity);
                if (iamParticipantOptional.isPresent()) {
                    DefaultParticipant participant = new DefaultParticipant(iamParticipantOptional.get().getIdentity());
                    participant.setMetadata(iamParticipantOptional.get().getMetadata());
                    participant.setPermissions(createPermissionsForParticipant(iamParticipantOptional.get()));

                    sink.success(participant);
                } else {
                    sink.error(new IllegalArgumentException("Participant cannot be found for the information provided"));
                }
            } catch (Exception e) {
                sink.error(e);
            }
        }));
    }

    private Permissions createPermissionsForParticipant(IamParticipant iamParticipant){
        Permissions ret = new Permissions();
        if(iamParticipant.getRoles() != null){
            for(Role role: iamParticipant.getRoles()){

                if(role.getAccessPolicies() != null){
                    for(AccessPolicy accessPolicy: role.getAccessPolicies()){

                        if(accessPolicy.getAllowedSendPatterns() != null){
                            for(AccessPattern accessPattern : accessPolicy.getAllowedSendPatterns()){
                                ret.addAllowedSendPattern(replaceVariables(accessPattern.getPattern(), iamParticipant));
                            }

                            for(AccessPattern accessPattern : accessPolicy.getAllowedSubscriptionPatterns()){
                                ret.addAllowedSubscriptionPattern(replaceVariables(accessPattern.getPattern(), iamParticipant));
                            }
                        }
                    }
                }
            }
        }
        return ret;
    }

    /**
     * Replace any variables in the pattern
     * ${} denotes a variable.
     * Right now we just support ${identity} more can be added as we need them
     */
    private String replaceVariables(String pattern, IamParticipant participant){
        String encodedIdentity = ContinuumUtil.safeEncodeURI(participant.getIdentity());
        return pattern.replace("${identity}", encodedIdentity);
    }

}

