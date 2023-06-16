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

package org.kinotic.continuum.core.api.security;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.kinotic.continuum.api.Identifiable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * Created by Navid Mitchell on 6/2/20
 */
public class Participant implements Identifiable<String> {

    private String id;
    private Map<String, String> metadata;
    private List<String> roles;

    public Participant() {
    }

    public Participant(String id) {
        this.id = id;
        metadata = new HashMap<>();
        roles = new ArrayList<>();
    }

    public Participant(String id, List<String> roles) {
        this.id = id;
        this.roles = roles;
    }

    public Participant(String id,
                       Map<String, String> metadata,
                       List<String> roles) {
        this.id = id;
        this.metadata = metadata;
        this.roles = roles;
    }

    /**
     * The identity of the participant
     * @return the identity of the participant
     */
    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * Metadata is a map of key value pairs that can be used to store additional information about a participant
     * @return a map of key value pairs
     */
    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }

    /**
     * Roles are a list of strings that can be used to authorize a participant to perform certain actions
     * @return a list of roles
     */
    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Participant that = (Participant) o;

        return new EqualsBuilder().append(id, that.id).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(id).toHashCode();
    }
}
