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

import com.kinotic.continuum.core.api.security.MetadataConstants;
import com.kinotic.continuum.iam.api.UserService;
import com.kinotic.continuum.iam.api.domain.Authenticator;
import com.kinotic.continuum.iam.api.domain.IamParticipant;
import com.kinotic.continuum.iam.api.domain.PasswordAuthenticator;
import com.kinotic.continuum.iam.api.domain.Role;
import com.kinotic.continuum.iam.internal.repositories.IamParticipantRepository;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

/**
 *
 * Created by Navid Mitchell on 3/3/20
 */
@Component
public class DefaultUserService extends AbstractIamParticipantService implements UserService {

    public DefaultUserService(IamParticipantRepository iamParticipantRepository,
                              PlatformTransactionManager transactionManager) {
        super(iamParticipantRepository, transactionManager);
    }

    @Override
    protected Map.Entry<String, String> getTypeMetadata() {
        return MetadataConstants.USER_TYPE;
    }

    @Override
    public Mono<IamParticipant> createNewUser(String identity, String password, List<Role> roles) {
        IamParticipant newParticipant = new IamParticipant(identity);
        Authenticator authenticator = new PasswordAuthenticator(BCrypt.hashpw(password, BCrypt.gensalt()));
        authenticator.setAccessKey(identity); // for passwords this is the same so we can find Authenticator easily
        authenticator.setIamParticipant(newParticipant);
        newParticipant.addAuthenticator(authenticator);
        newParticipant.putMetadata(MetadataConstants.USER_TYPE);
        newParticipant.setRoles(roles);

        return create(newParticipant);
    }

}
