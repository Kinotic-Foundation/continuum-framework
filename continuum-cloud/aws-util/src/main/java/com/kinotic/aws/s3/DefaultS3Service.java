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

import com.amazonaws.AbortedException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import com.kinotic.aws.AwsException;
import com.kinotic.util.UncheckedInterruptedException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.concurrent.Callable;

/**
 * Default implementation of {@link S3Service}
 *
 *
 * Created by navid on 9/11/19
 */
public class DefaultS3Service implements S3Service{
    private static final Logger log = LoggerFactory.getLogger(DefaultS3Service.class);

    private AmazonS3 s3Client;
    private long buildTime = 0;
    private AWSCredentialsProvider credentialsProvider = null;
    private Regions regions = null;

    public DefaultS3Service() {
        initS3Client();
    }

    public DefaultS3Service(AWSCredentialsProvider credentialsProvider) {
        this.credentialsProvider = credentialsProvider;
        initS3Client();
    }

    public DefaultS3Service(AWSCredentialsProvider credentialsProvider,
                            Regions regions) {
        this.credentialsProvider = credentialsProvider;
        this.regions = regions;
        initS3Client();
    }

    public DefaultS3Service(Regions regions) {
        this.regions = regions;
        initS3Client();
    }

    private synchronized void initS3Client(){
        // We keep track of the last time we built the client so
        // you don't get a condition where it gets rebuilt a lot really fast due to a failure when being used by a lot of clients
        long elapsedTime = System.currentTimeMillis() - buildTime;
        if(elapsedTime > 1000) {
            AmazonS3ClientBuilder builder = AmazonS3ClientBuilder.standard();
            if (credentialsProvider != null) {
                builder.withCredentials(credentialsProvider);
            }
            if (regions != null) {
                builder.withRegion(regions);
            }
            if(s3Client != null){
                try {
                    s3Client.shutdown();
                } catch (Exception e) {
                    log.warn("Error shutting down s3Client", e);
                }
            }
            s3Client = builder.build();
            buildTime = System.currentTimeMillis();
        }
    }

    private <V> V doWithRecovery(Callable<V> callable) throws Exception{
        V ret = null;
        int retries = 3;
        int tries = 0;
        boolean done = false;

        while(!done){
            try {
                tries++;

                ret = callable.call();

                done  = true;
            } catch (IllegalStateException ise) { // recoverable in some situations
                // This is caused by this https://github.com/aws/aws-sdk-java/issues/1282
                // it is not handled by client so we try to recover
                if(ise.getMessage().trim().equals("Connection pool shut down") && tries <= retries){
                    try {
                        Thread.sleep(1000);
                        log.warn("S3 client connection pool shutdown. Rebuilding Client!");
                        initS3Client();
                    } catch (InterruptedException e) {
                        // convert to unchecked version so caller can know thread was interrupted
                        throw new UncheckedInterruptedException(e);
                    }
                }else {
                    throw ise;
                }
            } catch (AbortedException ae){
                // If the thread throws an InterruptedException during the aws call
                // the aws code throws a AbortedException we convert it back to what our api expects here
                throw new UncheckedInterruptedException(ae);
            }
        }
        return ret;
    }

    @Override
    public void createBucketIfDoesNotExist(String bucketName) throws AwsException {

        String sanitizedBucket = sanitizeBucket(bucketName);
        boolean shouldCreateBucket = false;
        try {

            doWithRecovery(() -> s3Client.headBucket(new HeadBucketRequest(sanitizedBucket)));

        }catch (AmazonServiceException ase){
            // depending on the error we may need to create the bucket
            if(ase.getStatusCode() == 404){
                shouldCreateBucket = true;
            }else{
                throw new AwsException("Bucket with the same name already exists but is not accessible");
            }
        } catch (UncheckedInterruptedException uie){
            throw uie;
        } catch (Exception e) {
            throw new AwsException(e);
        }

        if(shouldCreateBucket) {
            try {
                doWithRecovery(() -> s3Client.createBucket(sanitizedBucket));
            } catch (UncheckedInterruptedException uie){
                throw uie;
            } catch (Exception e) {
                throw new AwsException(e);
            }
        }
    }

    @Override
    public void putFile(String bucket, String objectKey, File file) throws AwsException {
        try {
            // the object key can never begin with /
            String key = StringUtils.removeStart(objectKey, "/");

            doWithRecovery(() -> s3Client.putObject(sanitizeBucket(bucket), key, file));
            
        } catch (UncheckedInterruptedException uie){
            throw uie;
        } catch (Exception e) {
            throw new AwsException(e);
        }
    }

    @Override
    public S3Object getObject(String bucket, String objectKey)throws AwsException{
        try {
            return doWithRecovery(() -> s3Client.getObject(bucket, objectKey));
        } catch (UncheckedInterruptedException uie){
            throw uie;
        } catch (Exception e) {
            throw new AwsException(e);
        }
    }

    @Override
    public ObjectMetadata getObjectMetadata(String bucket, String objectKey)throws AwsException{
        try {
            return doWithRecovery(() -> s3Client.getObjectMetadata(bucket, objectKey));
        } catch (UncheckedInterruptedException uie){
            throw uie;
        } catch (Exception e) {
            throw new AwsException(e);
        }
    }

    @Override
    public URL getPreSignedURL(String bucket, String objectKey)throws AwsException {
        return getPreSignedURL(bucket, objectKey, Duration.ofHours(24));
    }

    @Override
    public URL getPreSignedURL(String bucket, String objectKey, Duration timeToExpire)throws AwsException{
        try {
            if(objectKey.startsWith("/")){
                objectKey = objectKey.substring(1);
            }

            // defaults to GET request.
            GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(sanitizeBucket(bucket), objectKey);
            generatePresignedUrlRequest.setExpiration(Date.from(Instant.now().plus(timeToExpire)));
            generatePresignedUrlRequest.rejectIllegalArguments();

            return doWithRecovery(() -> s3Client.generatePresignedUrl(generatePresignedUrlRequest));
        } catch (UncheckedInterruptedException uie){
            throw uie;
        } catch (Exception e) {
            throw new AwsException(e);
        }
    }


    private String sanitizeBucket(String bucket){
        return bucket.replace("/", "");
    }
}
