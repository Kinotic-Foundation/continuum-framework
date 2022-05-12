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

package com.kinotic.continuum.internal.core.api.support;

import com.kinotic.continuum.core.api.event.EventConstants;
import com.kinotic.continuum.core.api.security.MetadataConstants;
import com.kinotic.continuum.core.api.security.Participant;
import com.kinotic.continuum.core.api.security.Permissions;
import com.kinotic.continuum.core.api.security.SecurityService;
import com.kinotic.continuum.internal.core.api.security.DefaultParticipant;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * Created by Navíd Mitchell 🤪 on 7/16/21.
 */
@Component
public class MockSecurityService implements SecurityService {

    private final Map<String, Participant> participantMap = Map.of("testuser@kinotic.com",
                                                                   new DefaultParticipant("testuser@kinotic.com",
                                                                                          Map.ofEntries(MetadataConstants.USER_TYPE),
                                                                                          new Permissions().addAllowedSendPattern(EventConstants.SERVICE_DESTINATION_SCHEME+"://*.**")));

    @Override
    public Mono<Participant> authenticate(String accessKey, String secretToken) {
        if(participantMap.containsKey(accessKey)){
            return Mono.just(participantMap.get(accessKey));
        }else{
            return Mono.error(new IllegalArgumentException("Participant cannot be authenticated with information provided"));
        }
    }

    @Override
    public Mono<Participant> findParticipant(String participantIdentity) {
        if(participantMap.containsKey(participantIdentity)){
            return Mono.just(participantMap.get(participantIdentity));
        }else{
            return Mono.error(new IllegalArgumentException("Participant cannot be found for the information provided"));
        }
    }

}
