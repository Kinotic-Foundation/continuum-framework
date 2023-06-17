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

package org.kinotic.continuum.internal.core.api.security;

import org.kinotic.continuum.core.api.security.DefaultParticipant;
import org.kinotic.continuum.core.api.security.MetadataConstants;
import org.kinotic.continuum.core.api.security.Participant;
import org.kinotic.continuum.core.api.security.SecurityService;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Provided to make testing without a configured IAM easier
 * WARNING: should not be used in production for any reason
 * Created by Navid Mitchell on 3/11/20
 */
public class DummySecurityService implements SecurityService {

    @Override
    public CompletableFuture<Participant> authenticate(Map<String, String> authenticationInfo) {
        return CompletableFuture.completedFuture(new DefaultParticipant("coolTenant",
                                                                        "dummy",
                                                                        Map.of(MetadataConstants.TYPE_KEY, "dummy"),
                                                                        List.of("ADMIN")));
    }

}
