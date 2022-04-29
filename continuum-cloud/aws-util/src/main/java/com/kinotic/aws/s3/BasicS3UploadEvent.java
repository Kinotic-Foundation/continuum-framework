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

import java.nio.file.Path;

/**
 *
 * Created by navid on 9/17/19
 */
public class BasicS3UploadEvent {
    private String bucketName;
    private String objectKey;
    private Path path;

    public BasicS3UploadEvent(String bucketName, String objectKey, Path path) {
        this.bucketName = bucketName;
        this.objectKey = objectKey;
        this.path = path;
    }

    public String getBucketName() {
        return bucketName;
    }

    public String getObjectKey() {
        return objectKey;
    }

    public Path getPath() {
        return path;
    }
}
