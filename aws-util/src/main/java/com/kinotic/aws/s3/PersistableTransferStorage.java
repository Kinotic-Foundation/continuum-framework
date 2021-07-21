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

package com.kinotic.aws.s3;

import com.amazonaws.services.s3.transfer.PersistableTransfer;

/**
 * Provides an interfaces for clients to provide a mean to store and retrieve {@link PersistableTransfer} objects
 * Created by 🤓 on 5/13/21.
 */
public interface PersistableTransferStorage {

    /**
     * Checks if a {@link PersistableTransfer} has been stored for the values provided
     *
     * @param bucketName that the {@link PersistableTransfer} is associated with
     * @param key for the object in the bucket
     * @return true if a {@link PersistableTransfer} exists for the values provided
     */
    boolean hasExistingTransfer(String bucketName, String key);

    /**
     * Retrieve the existing {@link PersistableTransfer} or null if none exist
     *
     * @param bucketName that the {@link PersistableTransfer} is associated with
     * @param key for the object in the bucket
     * @return the {@link PersistableTransfer} that was stored
     */
    <T extends PersistableTransfer> T retrieve(String bucketName, String key);

    /**
     * Store the {@link PersistableTransfer}
     * NOTE: ideally this method should never block
     *
     * @param bucketName that the {@link PersistableTransfer} is associated with
     * @param key for the object in the bucket
     * @param persistableTransfer to store
     */
    void store(String bucketName, String key, PersistableTransfer persistableTransfer);

    /**
     * Remove the {@link PersistableTransfer} stored for the information provided
     * @param bucketName that the {@link PersistableTransfer} is associated with
     * @param key for the object in the bucket
     */
    void remove(String bucketName, String key);

}
