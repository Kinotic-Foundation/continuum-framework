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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kinotic.continuum.internal.util.SecurityUtil;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 *
 *
 * Created by Navid Mitchell on 3/10/20
 */
@Entity
@DiscriminatorValue("legacy")
public class LegacySharedSecretAuthenticator extends Authenticator {

    private byte[] sharedSecret;

    public LegacySharedSecretAuthenticator() {
    }

    @JsonIgnore
    public byte[] getSharedSecret() {
        return sharedSecret;
    }

    @JsonProperty
    public LegacySharedSecretAuthenticator setSharedSecret(byte[] sharedSecret) {
        this.sharedSecret = sharedSecret;
        return this;
    }

    @Override
    public boolean canAuthenticate(CharSequence secret) {
        String mac = getIamParticipant().getIdentity();
        // Fire legacy string to sign is
        String toSign = "minds" + mac;
        return SecurityUtil.canOtpSha1Authenticate(toSign, sharedSecret, secret.toString());
    }

}
