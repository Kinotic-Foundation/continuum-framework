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

import com.kinotic.continuum.core.api.security.Participant;
import com.kinotic.continuum.core.api.security.Permissions;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * Created by navid on 2/3/20
 */
public class DefaultParticipant implements Participant {

    private String identity;

    private Map<String, String> metadata;

    private Permissions permissions;

    public DefaultParticipant() {
    }

    public DefaultParticipant(String identity) {
        this.identity = identity;
        metadata = new HashMap<>();
        permissions = new Permissions();
    }

    public DefaultParticipant(String identity, Map<String, String> metadata) {
        this.identity = identity;
        this.metadata = metadata;
        permissions = new Permissions();
    }

    public DefaultParticipant(String identity, Permissions permissions) {
        this.identity = identity;
        this.permissions = permissions;
        metadata = new HashMap<>();
    }

    public DefaultParticipant(String identity,
                              Map<String, String> metadata,
                              Permissions permissions) {
        this.identity = identity;
        this.metadata = metadata;
        this.permissions = permissions;
    }

    @Override
    public String getIdentity() {
        return identity;
    }

    public Participant setIdentity(String identity) {
        this.identity = identity;
        return this;
    }

    @Override
    public Map<String, String> getMetadata() {
        return metadata;
    }

    public Participant setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
        return this;
    }

    @Override
    public Permissions getPermissions() {
        return permissions;
    }

    public Participant setPermissions(Permissions permissions) {
        this.permissions = permissions;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof Participant)) return false;

        Participant that = (Participant) o;

        return new EqualsBuilder().append(identity, that.getIdentity()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(identity).toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("identity", identity)
                .append("metadata", metadata)
                .toString();
    }
}
