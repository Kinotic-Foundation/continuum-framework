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

package com.kinotic.continuum.core.api.security;

import org.apache.commons.lang3.Validate;

import java.util.LinkedList;
import java.util.List;

/**
 * {@link Permissions} define access that a given authenticated participant has
 *
 * TODO: DOC pattern specification
 *
 * Created by navid on 1/22/20
 */
public class Permissions {

    private List<String> allowedSendPatterns;

    private List<String> allowedSubscriptionPatterns;

    public Permissions() {
        allowedSendPatterns = new LinkedList<>();
        allowedSubscriptionPatterns = new LinkedList<>();
    }

    public Permissions(List<String> allowedSendPatterns, List<String> allowedSubscriptionPatterns) {
        Validate.notNull(allowedSendPatterns);
        Validate.notNull(allowedSubscriptionPatterns);
        this.allowedSendPatterns = allowedSendPatterns;
        this.allowedSubscriptionPatterns = allowedSubscriptionPatterns;
    }

    public Permissions addAllowedSendPattern(String pattern){
        this.allowedSendPatterns.add(pattern);
        return this;
    }

    public List<String> getAllowedSendPatterns() {
        return allowedSendPatterns;
    }

    public Permissions setAllowedSendPatterns(List<String> allowedSendPatterns) {
        this.allowedSendPatterns = allowedSendPatterns;
        return this;
    }

    public Permissions addAllowedSubscriptionPattern(String pattern){
        this.allowedSubscriptionPatterns.add(pattern);
        return this;
    }

    public List<String> getAllowedSubscriptionPatterns() {
        return allowedSubscriptionPatterns;
    }

    public Permissions setAllowedSubscriptionPatterns(List<String> allowedSubscriptionPatterns) {
        this.allowedSubscriptionPatterns = allowedSubscriptionPatterns;
        return this;
    }
}
