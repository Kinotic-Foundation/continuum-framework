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

import com.kinotic.continuum.core.api.CRI;
import com.kinotic.continuum.core.api.security.Participant;
import com.kinotic.continuum.core.api.security.Session;
import com.kinotic.continuum.internal.util.SecurityUtil;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.http.server.PathContainer;
import org.springframework.web.util.pattern.PathPattern;

import java.util.Date;
import java.util.List;

/**
 *
 * Created by navid on 1/23/20
 */
public abstract class AbstractSession implements Session {

    private final Participant participant;
    private final String sessionId;
    private final String sessionSecret;
    private final PathContainer.Options parseOptions;
    private final List<PathPattern> sendPathPatterns;
    private final List<PathPattern> subscribePathPatterns;

    protected Date lastUsedDate;

    public AbstractSession(Participant participant,
                           String sessionId,
                           PathContainer.Options parseOptions,
                           List<PathPattern> sendPathPatterns,
                           List<PathPattern> subscribePathPatterns) {
        this.participant = participant;
        this.sessionId = sessionId;
        this.sessionSecret = SecurityUtil.generateRandomPassword(64);
        this.parseOptions = parseOptions;
        this.sendPathPatterns = sendPathPatterns;
        this.subscribePathPatterns = subscribePathPatterns;
        this.lastUsedDate = new Date();
    }

    @Override
    public Participant participant(){
        return participant;
    }

    @Override
    public String sessionId(){
        return sessionId;
    }

    @Override
    public String sessionSecret() {
        return sessionSecret;
    }

    @Override
    public Date lastUsedDate() {
        return lastUsedDate;
    }

    @Override
    public boolean sendAllowed(CRI cri){
        Validate.notNull(cri, "The CRI must not be null");
        return checkMatches(cri.raw(), sendPathPatterns);
    }

    @Override
    public boolean subscribeAllowed(CRI cri){
        Validate.notNull(cri, "The CRI must not be null");
        return checkMatches(cri.raw(), subscribePathPatterns);
    }

    private boolean checkMatches(String cri, List<PathPattern> patterns){
        boolean ret = false;
        PathContainer pathContainer = PathContainer.parsePath(cri, parseOptions);
        for(PathPattern pathPattern : patterns){
            if(pathPattern.matches(pathContainer)){
                ret = true;
                break;
            }
        }
        return ret;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof Session)) return false;

        Session that = (Session) o;

        return new EqualsBuilder().append(participant, that.participant())
                                  .append(sessionId, that.sessionId())
                                  .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(participant).append(sessionId).toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("sessionId", sessionId)
                .append("lastUsedDate", lastUsedDate)
                .append("participant", participant)
                .toString();
    }
}
