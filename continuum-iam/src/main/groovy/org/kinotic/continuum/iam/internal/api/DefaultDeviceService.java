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

import org.kinotic.continuum.core.api.security.MetadataConstants;
import org.kinotic.continuum.iam.api.DeviceRegistrationConstants;
import org.kinotic.continuum.iam.api.DeviceService;
import org.kinotic.continuum.iam.api.config.ContinuumIamProperties;
import com.kinotic.continuum.iam.api.domain.*;
import org.kinotic.continuum.iam.api.domain.*;
import org.kinotic.continuum.iam.internal.repositories.IamParticipantRepository;
import org.kinotic.continuum.iam.internal.repositories.RoleRepository;
import org.kinotic.continuum.internal.util.SecurityUtil;
import org.kinotic.continuum.internal.utils.ReactorUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import reactor.core.publisher.Mono;

import java.util.*;

/**
 *
 * Created by Navid Mitchell on 3/3/20
 */
@Component
public class DefaultDeviceService extends AbstractIamParticipantService implements DeviceService {

    private final ContinuumIamProperties properties;

    private final RoleRepository roleRepository;

    public DefaultDeviceService(IamParticipantRepository iamParticipantRepository,
                                PlatformTransactionManager transactionManager,
                                ContinuumIamProperties properties,
                                RoleRepository roleRepository) {
        super(iamParticipantRepository, transactionManager);
        this.properties = properties;
        this.roleRepository = roleRepository;
    }

    @Override
    protected Map.Entry<String, String> getTypeMetadata() {
        return MetadataConstants.DEVICE_TYPE;
    }

    @Override
    public Mono<IamParticipant> createNewDevice(String identity, List<Authenticator> authenticators, List<Role> roles) {
        IamParticipant newParticipant = new IamParticipant(identity);
        newParticipant.setAuthenticators(authenticators);
        newParticipant.putMetadata(MetadataConstants.DEVICE_TYPE);
        newParticipant.setRoles(roles);
        return create(newParticipant);
    }

    @Override
    public Mono<List<RegistrationProperty>> registerDeviceWithSharedSecretAuth(String identity) {
        return Mono.create(sink -> transactionTemplate.executeWithoutResult(status -> {
            try {
                Optional<Role> defaultDeviceRole  = roleRepository.findById(DomainConstants.DEFAULT_DEVICE_ROLE_ID);

                if(defaultDeviceRole.isPresent()){
                    IamParticipant device = new IamParticipant(identity);
                    device.putMetadata(MetadataConstants.DEVICE_TYPE);

                    LegacySharedSecretAuthenticator legacyAuthenticator = new LegacySharedSecretAuthenticator();
                    legacyAuthenticator.setAccessKey(UUID.randomUUID().toString());
                    legacyAuthenticator.setSharedSecret(SecurityUtil.generateSecretKey(256));
                    device.addAuthenticator(legacyAuthenticator);

                    device.setRoles(Collections.singletonList(defaultDeviceRole.get()));

                    create(device).map(iamParticipant -> {
                        // We want to return the new access credentials to the caller
                        LegacySharedSecretAuthenticator authenticator = (LegacySharedSecretAuthenticator) iamParticipant.getAuthenticators().get(0);

                        List<RegistrationProperty> ret = new ArrayList<>(2);
                        ret.add(new RegistrationProperty(DeviceRegistrationConstants.ACCESS_KEY, authenticator.getAccessKey()));
                        ret.add(new RegistrationProperty(DeviceRegistrationConstants.SECRET_KEY, new String(authenticator.getSharedSecret())));
                        return ret;

                    }).subscribe(ReactorUtils.monoSinkToSubscriber(sink));

                }else{
                    sink.error(new IllegalStateException(DomainConstants.DEFAULT_DEVICE_ROLE_ID + " is not available. You must create this first."));
                }
            } catch (Exception e) {
                sink.error(e);
            }
        }));
    }

}
