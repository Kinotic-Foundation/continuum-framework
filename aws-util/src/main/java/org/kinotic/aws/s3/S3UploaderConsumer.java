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

package org.kinotic.aws.s3;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 *
 * Created by navid on 9/17/19
 */
public class S3UploaderConsumer implements Function<Path, Boolean> {
    private static final Logger log = LoggerFactory.getLogger(S3UploaderConsumer.class);

    private final String sourceDirectory;
    private final String bucketName;
    private final String objectPrefix;
    private final S3Service s3Service;
    private final Consumer<BasicS3UploadEvent> uploadEventConsumer;

    public S3UploaderConsumer(String sourceDirectory,
                              String bucketName,
                              String objectPrefix,
                              S3Service s3Service,
                              Consumer<BasicS3UploadEvent> uploadEventConsumer) {
        Validate.notBlank(sourceDirectory,"The sourceDirectory must be specified");
        Validate.notBlank(bucketName,"The bucketName must be specified");
        // make sure we mirror the dir structure exactly
        this.sourceDirectory = Paths.get(sourceDirectory).toAbsolutePath().toString();
        this.bucketName = bucketName;
        this.objectPrefix = objectPrefix;
        this.s3Service = s3Service;
        this.uploadEventConsumer = uploadEventConsumer;
    }

    @Override
    public Boolean apply(Path path) {
        String objectKey = getObjectKey(path);
        if(log.isTraceEnabled()){
            log.trace("Uploading... \n\t"+ path +"\n\tto\n\t"+bucketName+"/"+objectKey);
        }

        s3Service.putFile(bucketName, objectKey, path.toFile());

        if(uploadEventConsumer != null) {
            uploadEventConsumer.accept(new BasicS3UploadEvent(bucketName, objectKey, path));
        }

        return true;
    }

    private String getObjectKey(Path path){
        String ret = path.toAbsolutePath().toString();
        ret = StringUtils.removeStart(ret, sourceDirectory);
        ret = StringUtils.removeStart(ret,"/");
        return (this.objectPrefix == null ? "" : this.objectPrefix)+ret;
    }

}
