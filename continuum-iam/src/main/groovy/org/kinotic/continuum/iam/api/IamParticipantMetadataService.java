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
import org.kinotic.continuum.api.annotations.Version;

import java.util.Map;

/**
 * Created by 🤓 on 6/13/21.
 */
@Publish
@Version("0.1.0")
public interface IamParticipantMetadataService {

    /**
     * Find the metadata for the given participant
     * @param identity of the participant to find the metadata for
     * @return the metadata or null if no participant for the given identity exists
     */
    Map<String, String> findMetadataForParticipant(String identity);

    /**
     * Find the metadata for for the given participant and key
     * @param identity of the participant to find the metadata value for
     * @param key the key of the metadata value to find
     * @return the metadata value or null if no metadata value exists or no participant for the given identity exists
     */
    String findMetadataValueForParticipant(String identity, String key);

    /**
     * Creates or updates the value in the metadata for the given participant and key
     * @param identity of the participant to update the metadata for
     * @param key the key of the metadata value to update or create
     * @param value to use for the metadata
     * @return true if the participant could be found, false if there is no participant for the given identity
     */
    boolean createOrUpdateMetadataValueForParticipant(String identity, String key, String value);

    /**
     * Deletes the given key and value for the participant
     * @param identity of the participant to delete the metadata for
     * @param key the key of the metadata value to remove
     * @return true if the participant could be found, false if there is no participant for the given identity
     */
    boolean removeMetadataValueForParticipant(String identity, String key);

}
