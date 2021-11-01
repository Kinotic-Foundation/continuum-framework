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

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.kinotic.aws.AwsException;
import com.kinotic.aws.UncheckedInterruptedException;

import java.io.File;
import java.net.URL;
import java.time.Duration;

/**
 * S3 Service to simplify tasks when working with S3
 * Maintains a single {@link com.amazonaws.services.s3.AmazonS3} client for all requests
 *
 *
 * Created by navid on 9/11/19
 */
public interface S3Service {

    /**
     * Creates an s3 bucket if it does not already exist
     * @param bucketName name of the bucket to create
     * @throws AwsException if an exception occurs
     * @throws UncheckedInterruptedException if the any operation threw an {@link InterruptedException}
     */
    void createBucketIfDoesNotExist(String bucketName) throws AwsException, UncheckedInterruptedException;

    /**
     * Stores the data from the {@link File} in S3. The file will be written using the internal authentication credentials
     *
     * <p>
     * The client automatically computes
     * a checksum of the file.
     * Amazon S3 uses checksums to validate the data in each file.
     * </p>
     *
     * @param bucket the name of the bucket that the file will be stored in
     * @param objectKey the key that will represent the object within S3
     * @param file to upload
     * @throws AwsException if an exception occurs during upload to s3
     * @throws UncheckedInterruptedException if the any operation threw an {@link InterruptedException}
     */
    void putFile(String bucket, String objectKey, File file) throws AwsException, UncheckedInterruptedException;

    /**
     * Gets a s3 file at the given location
     * @param bucket the bucket that the resource exists in
     * @param objectKey the path to the object that URL should be generated for
     * @return the {@link S3Object} for the given bucket and objectKey
     * @throws AwsException if an exception occurs
     * @throws UncheckedInterruptedException if the any operation threw an {@link InterruptedException}
     */
    S3Object getObject(String bucket, String objectKey)throws AwsException, UncheckedInterruptedException;

    /**
     * Returns the object metadata for the given bucket and key
     * @param bucket the bucket that the resource exists in
     * @param objectKey the path to the object that URL should be generated for
     * @return the {@link ObjectMetadata} for the given bucket and objectKey
     * @throws AwsException if an exception occurs
     * @throws UncheckedInterruptedException if the any operation threw an {@link InterruptedException}
     */
    ObjectMetadata getObjectMetadata(String bucket, String objectKey)throws AwsException, UncheckedInterruptedException;

    /**
     * Returns a pre-signed temporary URL based upon the information provided
     * NOTE: the URL will expire in 24 hours
     * @param bucket the bucket that the resource exists in
     * @param objectKey the path to the object that URL should be generated for
     * @return the pre-signed URL
     * @throws AwsException if an exception occurs
     * @throws UncheckedInterruptedException if the any operation threw an {@link InterruptedException}
     */
    URL getPreSignedURL(String bucket, String objectKey)throws AwsException, UncheckedInterruptedException;

    /**
     * Returns a pre-signed temporary URL based upon the information provided
     * @param bucket the bucket that the resource exists in
     * @param objectKey the path to the object that URL should be generated for
     * @param timeToExpire the duration of time that must elapse before the {@link URL} expires
     * @return the pre-signed URL
     * @throws AwsException if an exception occurs
     * @throws UncheckedInterruptedException if the any operation threw an {@link InterruptedException}
     */
    URL getPreSignedURL(String bucket, String objectKey, Duration timeToExpire)throws AwsException, UncheckedInterruptedException;

}
