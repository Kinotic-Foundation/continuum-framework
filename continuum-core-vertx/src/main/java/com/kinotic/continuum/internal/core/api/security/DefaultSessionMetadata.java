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

package com.kinotic.continuum.internal.core.api.security;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kinotic.continuum.core.api.security.SessionMetadata;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.ignite.cache.query.annotations.QuerySqlField;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * Created by Navid Mitchell on 6/2/20
 */
public class DefaultSessionMetadata implements Serializable, SessionMetadata {

    @JsonProperty
    @QuerySqlField(index = true)
    private String sessionId;

    @JsonProperty
    @QuerySqlField
    private String participantIdentity;

    @JsonProperty
    @QuerySqlField
    private String participantType;

    @JsonProperty
    @QuerySqlField
    private Date lastUsedDate;

    public DefaultSessionMetadata() {
    }

    @Override
    public String sessionId() {
        return sessionId;
    }

    public DefaultSessionMetadata sessionId(String sessionId) {
        this.sessionId = sessionId;
        return this;
    }

    @Override
    public String participantIdentity() {
        return participantIdentity;
    }

    public DefaultSessionMetadata participantIdentity(String participantIdentity) {
        this.participantIdentity = participantIdentity;
        return this;
    }

    @Override
    public String participantType() {
        return participantType;
    }

    public DefaultSessionMetadata participantType(String participantType) {
        this.participantType = participantType;
        return this;
    }

    @Override
    public Date lastUsedDate() {
        return lastUsedDate;
    }

    public DefaultSessionMetadata lastUsedDate(Date lastUsedDate) {
        this.lastUsedDate = lastUsedDate;
        return this;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("sessionId", sessionId)
                .append("participantIdentity", participantIdentity)
                .append("participantType", participantType)
                .append("lastUsedDate", lastUsedDate)
                .toString();
    }
}
