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

import com.kinotic.continuum.iam.api.IamParticipantMetadataService;
import com.kinotic.continuum.iam.api.domain.IamParticipant;
import com.kinotic.continuum.iam.internal.repositories.IamParticipantRepository;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

/**
 * Created by 🤓 on 6/13/21.
 */
@Component
public class DefaultIamParticipantMetadataService implements IamParticipantMetadataService {

    private final IamParticipantRepository iamParticipantRepository;

    public DefaultIamParticipantMetadataService(IamParticipantRepository iamParticipantRepository) {
        this.iamParticipantRepository = iamParticipantRepository;
    }

    @Override
    public Map<String, String> findMetadataForParticipant(String identity) {
        Map<String, String> ret = null;
        Optional<IamParticipant> result = iamParticipantRepository.findById(identity);
        if(result.isPresent()){
            ret = result.get().getMetadata();
        }
        return ret;
    }

    @Override
    public String findMetadataValueForParticipant(String identity, String key) {
        String ret = null;
        Map<String, String> metadata = findMetadataForParticipant(identity);
        if(metadata != null){
            ret = metadata.get(key);
        }
        return ret;
    }

    @Override
    public boolean createOrUpdateMetadataValueForParticipant(String identity, String key, String value) {
        boolean ret = false;
        Optional<IamParticipant> result = iamParticipantRepository.findById(identity);
        if(result.isPresent()){
            IamParticipant participant = result.get();
            participant.getMetadata().put(key, value);
            iamParticipantRepository.save(participant);
            ret = true;
        }
        return ret;
    }

    @Override
    public boolean removeMetadataValueForParticipant(String identity, String key) {
        boolean ret = false;
        Optional<IamParticipant> result = iamParticipantRepository.findById(identity);
        if(result.isPresent()){
            IamParticipant participant = result.get();
            participant.getMetadata().remove(key);
            iamParticipantRepository.save(participant);
            ret = true;
        }
        return ret;
    }
}
