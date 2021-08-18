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

package com.kinotic.continuum.core.api;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 *
 * Created by Navid Mitchell on 5/1/20
 */
class DefaultCRI implements CRI {

    private final String raw;
    private final Scheme scheme;
    private final String scope;
    private final String resourceName;
    private final String version;
    private final String path;

    public DefaultCRI(Scheme scheme, String scope, String resourceName, String version, String path) {

        // TODO: validate characters in vars.. ie version cannot contain / or else path logic breaks down

        Validate.notNull(scheme, "The scheme must not be null");
        Validate.notBlank(resourceName, "The resourceName must not be null or blank");
        this.scheme = scheme;
        this.scope = scope;
        this.resourceName = resourceName;
        this.version = version;
        this.path = path;
        this.raw = scheme.raw() + "://"
                                + (this.scope != null && !this.scope.isEmpty() ? this.scope + "@" : "")
                                + resourceName
                                + (this.version != null && !this.version.isEmpty() ? "#" + this.version : "")
                                + (this.path != null ? "/" + this.path : "");
    }

    /**
     * Will create a {@link CRI} from a raw string
     *
     * @param rawURC the raw string to create from an {@link CRI}
     */
    public DefaultCRI(String rawURC) {
        Validate.notNull(rawURC, "The string provided must not be null");
        this.raw = rawURC;
        String temp = rawURC;

        // Find scheme
        int schemeEndIdx = temp.indexOf("://");
        Validate.isTrue(schemeEndIdx != -1, "URC: \""+rawURC+"\" format is invalid. URC must begin with scheme://");

        scheme = Scheme.create(temp.substring(0, schemeEndIdx));
        temp = temp.substring(schemeEndIdx+3);

        // find scope if provided
        int atIdx = temp.indexOf("@");
        if(atIdx != -1){
            scope = temp.substring(0,atIdx);
            temp = temp.substring(atIdx+1);
        }else{
            scope = null;
        }

        // find version if provided
        int hashIdx = temp.indexOf("#");
        if(hashIdx != -1){
            resourceName = temp.substring(0, hashIdx);
            temp = temp.substring(hashIdx+1);

            // version is until the path
            int pathIndex = temp.indexOf("/");
            if(pathIndex != -1){
                version = temp.substring(0, pathIndex);
                temp = temp.substring(pathIndex+1);
            }else{
                version = temp;
                temp = "";
            }
        }else{
            version = null;
            // since no version we still need to extract the resource name
            // which is the remaining string or until the path
            int pathIndex = temp.indexOf("/");
            if(pathIndex != -1){
                resourceName = temp.substring(0, pathIndex);
                temp = temp.substring(pathIndex+1);
            }else{
                resourceName = temp;
                temp = "";
            }
        }

        // if anything is left in temp it is the path
        if(!temp.isEmpty()){
            path = temp;
        }else{
            path = null;
        }
    }

    @Override
    public Scheme scheme() {
        return scheme;
    }

    @Override
    public String scope() {
        return scope;
    }

    @Override
    public boolean hasScope() {
        return scope != null && !scope.isEmpty();
    }

    @Override
    public String resourceName() {
        return resourceName;
    }

    @Override
    public String version() {
        return version;
    }

    @Override
    public boolean hasVersion() {
        return version != null && !version.isEmpty();
    }

    @Override
    public String path() {
        return path;
    }

    @Override
    public boolean hasPath() {
        return path != null && !path.isEmpty();
    }

    @Override
    public String baseResource() {
        return scheme.raw() + "://" +
               (hasScope() ? scope + "@" : "") +
               resourceName;
    }

    @Override
    public String versionedResource() {
        return scheme.raw() + "://" +
               (hasScope() ? scope + "@" : "") +
               resourceName +
               (hasVersion() ? "#" + version : "");
    }

    @Override
    public String raw() {
        return raw;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        DefaultCRI that = (DefaultCRI) o;

        return new EqualsBuilder()
                .append(raw, that.raw)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(raw)
                .toHashCode();
    }

    @Override
    public String toString() {
        return raw;
    }
}
