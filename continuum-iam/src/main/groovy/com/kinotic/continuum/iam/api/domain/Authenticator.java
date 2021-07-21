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

package com.kinotic.continuum.iam.api.domain;

import com.fasterxml.jackson.annotation.*;

import javax.persistence.*;

/**
 *
 * Created by navid on 1/30/20
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = PasswordAuthenticator.class, name = "password"),
        @JsonSubTypes.Type(value = LegacySharedSecretAuthenticator.class, name = "legacy")
})
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public abstract class Authenticator {

    @Id
    private String accessKey;

    @Version
    private Long version;

    @ManyToOne
    private IamParticipant iamParticipant;

    public Authenticator() {
    }

    /**
     * This is the value used to lookup this {@link Authenticator}
     * For a {@link PasswordAuthenticator} this will usually be the identity of the {@link IamParticipant}
     * For a {@link LegacySharedSecretAuthenticator} the accessKey will be unique
     * @return
     */
    public String getAccessKey() {
        return accessKey;
    }

    public Authenticator setAccessKey(String accessKey) {
        this.accessKey = accessKey;
        return this;
    }

    public Long getVersion() {
        return version;
    }

    public Authenticator setVersion(Long version) {
        this.version = version;
        return this;
    }

    @JsonIgnore
    public IamParticipant getIamParticipant() {
        return iamParticipant;
    }

    @JsonProperty
    public Authenticator setIamParticipant(IamParticipant iamParticipant) {
        this.iamParticipant = iamParticipant;
        return this;
    }

    /**
     * Determine if the given secret is valid for authentication
     * @param secret the secret to verify
     * @return true if the secret is valid for authentication false if not
     */
    public abstract boolean canAuthenticate(CharSequence secret);

}
