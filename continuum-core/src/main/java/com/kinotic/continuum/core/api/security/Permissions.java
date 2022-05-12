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
 * {@link Permissions} define access that a given authenticated participant has.
 * The send and subscription patterns specify where a {@link Participant} can send Events and where they can subscribe to for Events.
 *
 * All permissions "Patterns" use Springs PathPattern parsing semantics to match an event to allowed destinations.
 * If a destination matches an allowed pattern supplied by the {@link Permissions} the send or subscribe is allowed.
 *
 * The matching semantics are as follows.
 *
 * Representation of a parsed path pattern. Includes a chain of path elements
 * for fast matching and accumulates computed state for quick comparison of
 * patterns.
 *
 * <p>{@code PathPattern} matches URL paths using the following rules:<br>
 * <ul>
 * <li>{@code ?} matches one character</li>
 * <li>{@code *} matches zero or more characters within a path segment</li>
 * <li>{@code **} matches zero or more <em>path segments</em> until the end of the path</li>
 * <li><code>{spring}</code> matches a <em>path segment</em> and captures it as a variable named "spring"</li>
 * <li><code>{spring:[a-z]+}</code> matches the regexp {@code [a-z]+} as a path variable named "spring"</li>
 * <li><code>{*spring}</code> matches zero or more <em>path segments</em> until the end of the path
 * and captures it as a variable named "spring"</li>
 * </ul>
 *
 * <p><strong>Note:</strong> In contrast to
 * {@link org.springframework.util.AntPathMatcher}, {@code **} is supported only
 * at the end of a pattern. For example {@code /pages/{**}} is valid but
 * {@code /pages/{**}/details} is not. The same applies also to the capturing
 * variant <code>{*spring}</code>. The aim is to eliminate ambiguity when
 * comparing patterns for specificity.
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
