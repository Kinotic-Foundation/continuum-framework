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

package org.kinotic.continuum.iam.api;

import org.kinotic.continuum.api.annotations.Publish;
import org.kinotic.continuum.core.api.crud.CrudService;
import org.kinotic.continuum.iam.api.domain.Authenticator;
import org.kinotic.continuum.iam.api.domain.IamParticipant;
import org.kinotic.continuum.iam.api.domain.RegistrationProperty;
import org.kinotic.continuum.iam.api.domain.Role;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 *
 * Created by navid on 2/3/20
 */
@Publish
public interface DeviceService extends CrudService<IamParticipant, String> {


    /**
     * Creates a new {@link IamParticipant} that is setup as a device with default permissions
     * @param identity the identity for the device typically a mac address
     * @param authenticators that can be used to authenticate this device
     * @param roles for the new device
     *
     * @return a {@link Mono} containing the newly created and persisted {@link IamParticipant} or an error if a exception occurred
     */
    CompletableFuture<IamParticipant> createNewDevice(String identity, List<Authenticator> authenticators, List<Role> roles);


    /**
     * Registers a new device that will authenticate with a shared secret.
     * @param identity the identity of the device to register
     * @return a {@link Mono} containing a {@link List<Map.Entry<String,String>>} containing the information needed for the device to authenticate
     */
    CompletableFuture<List<RegistrationProperty>> registerDeviceWithSharedSecretAuth(String identity);


}
