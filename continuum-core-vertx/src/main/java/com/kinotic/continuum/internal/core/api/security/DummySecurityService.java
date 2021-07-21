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

package com.kinotic.continuum.internal.core.api.security;

import com.kinotic.continuum.core.api.Scheme;
import com.kinotic.continuum.core.api.security.MetadataConstants;
import com.kinotic.continuum.core.api.security.Participant;
import com.kinotic.continuum.core.api.security.Permissions;
import com.kinotic.continuum.core.api.security.SecurityService;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

/**
 * Provided to make testing without a configured IAM easier
 * WARNING: should not be used in production for any reason
 *
 *
 * Created by Navid Mitchell on 3/11/20
 */
public class DummySecurityService implements SecurityService {

    @Override
    public Mono<Participant> authenticate(String accessKey, String secretToken) {
        return Mono.just(new DefaultParticipant(accessKey,
                                                Map.of(MetadataConstants.TYPE_KEY, "dummy"),
                                                new Permissions(List.of(Scheme.SERVICE.raw() + "://*.**", Scheme.STREAM.raw() + "://*.**"),
                                                                List.of(Scheme.SERVICE.raw() + "://*.**", Scheme.STREAM.raw() + "://*.**"))
        ));
    }

    @Override
    public Mono<Participant> findParticipant(String participantIdentity) {
        return Mono.just(new DefaultParticipant(participantIdentity,
                                                Map.of(MetadataConstants.TYPE_KEY, "dummy"),
                                                new Permissions(List.of(Scheme.SERVICE.raw() + "://*.**", Scheme.STREAM.raw() + "://*.**"),
                                                                List.of(Scheme.SERVICE.raw() + "://*.**", Scheme.STREAM.raw() + "://*.**"))
        ));
    }
}
