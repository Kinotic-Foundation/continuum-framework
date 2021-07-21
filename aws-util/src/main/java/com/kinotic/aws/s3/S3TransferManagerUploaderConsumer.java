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

import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.transfer.PersistableTransfer;
import com.amazonaws.services.s3.transfer.PersistableUpload;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;
import com.amazonaws.services.s3.transfer.internal.S3SyncProgressListener;
import com.kinotic.aws.UncheckedInterruptedException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Provides a Consumer that will use the AWS {@link TransferManager} to upload files to S3
 *
 * NOTE: If you set {@link com.amazonaws.services.s3.transfer.TransferManagerBuilder#setMinimumUploadPartSize}
 *       And an upload is resumed that was stored in the {@link PersistableTransferStorage}
 *       it will change the setMinimumUploadPartSize to what is set in the {@link PersistableUpload}.
 *       This means if you change the size between uses you may need to clear your {@link PersistableTransferStorage}
 *
 *
 *
 * Created by navid on 9/17/19
 */
public class S3TransferManagerUploaderConsumer implements Function<Path, Boolean> {
    private static final Logger log = LoggerFactory.getLogger(S3TransferManagerUploaderConsumer.class);

    private String sourceDirectory;
    private String bucketName;
    private String objectPrefix;
    private TransferManager transferManager;
    private Consumer<BasicS3UploadEvent> uploadEventConsumer;
    private PersistableTransferStorage persistableTransferStorage;

    public S3TransferManagerUploaderConsumer(String sourceDirectory,
                                             String bucketName,
                                             String objectPrefix,
                                             TransferManager transferManager,
                                             Consumer<BasicS3UploadEvent> uploadEventConsumer,
                                             PersistableTransferStorage persistableTransferStorage) {
        Validate.notBlank(sourceDirectory,"The sourceDirectory must be specified");
        Validate.notBlank(bucketName,"The bucketName must be specified");
        Validate.notNull(transferManager, "The transferManager must not be null");

        // make sure we mirror the dir structure exactly
        this.sourceDirectory = Paths.get(sourceDirectory).toAbsolutePath().toString();
        this.bucketName = bucketName;
        this.objectPrefix = objectPrefix;
        this.transferManager = transferManager;
        this.uploadEventConsumer = uploadEventConsumer;
        this.persistableTransferStorage = persistableTransferStorage;
    }

    @Override
    public Boolean apply(Path path) {
        boolean finished = false;
        String objectKey = getObjectKey(path);
        if(log.isTraceEnabled()){
            log.trace("Uploading... \n\t"+path.toString()+"\n\tto\n\t"+bucketName+"/"+objectKey);
        }

        PersistableUpload persistableUpload = null;
        if(persistableTransferStorage != null
            && persistableTransferStorage.hasExistingTransfer(bucketName, objectKey)){
            persistableUpload = persistableTransferStorage.retrieve(bucketName, objectKey);
        }

        Upload upload;
        if(persistableUpload != null){
            upload = transferManager.resumeUpload(persistableUpload);
        }else{
            PutObjectRequest por = new PutObjectRequest(bucketName, objectKey, path.toFile());
            upload = transferManager.upload(por, new S3SyncProgressListener() {
                @Override
                public void onPersistableTransfer(PersistableTransfer persistableTransfer) {
                    if(persistableTransferStorage != null){
                        persistableTransferStorage.store(bucketName, objectKey, persistableTransfer);
                    }
                }
            });
        }

        try {
            upload.waitForCompletion();
            finished = true;

            if(persistableTransferStorage != null){
                persistableTransferStorage.remove(bucketName, objectKey);
            }

            if(uploadEventConsumer != null) {
                uploadEventConsumer.accept(new BasicS3UploadEvent(bucketName, objectKey, path));
            }

        } catch (InterruptedException e) {
            throw new UncheckedInterruptedException(e);
        } catch (Exception e){
            // any other error will cause the file to be moved to the failed directory so we just log
            // the exception since this logic supports resume functionality
            log.error("Exception occurred processing file "+path.toString(), e);
        }

        return finished;
    }

    private String getObjectKey(Path path){
        String ret = path.toAbsolutePath().toString();
        ret = StringUtils.removeStart(ret, sourceDirectory);
        ret = StringUtils.removeStart(ret,"/");
        return (this.objectPrefix == null ? "" : this.objectPrefix)+ret;
    }

}
