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

import com.kinotic.continuum.api.Identifiable;
import lombok.Data;
import lombok.NonNull;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.Validate;

import javax.persistence.*;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * Created by navid on 2/3/20
 */
@Entity
@Data
@Accessors(chain = true)
public class Role implements Identifiable<String> {

    @Id
    @NonNull
    private String identity;

    @Version
    @NonNull
    private Long version;

    private String description;

    @ManyToMany
    private List<AccessPolicy> accessPolicies = new LinkedList<>();

    public Role() {
    }

    public Role(String identity){
        Validate.notBlank(identity, "The identity provided must not be blank");
        this.identity = identity;
    }

    public Role addAccessPolicy(AccessPolicy accessPolicy){
        this.accessPolicies.add(accessPolicy);
        return this;
    }

}
