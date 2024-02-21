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

package org.kinotic.continuum.idl.api.schema;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.*;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.Validate;
import org.kinotic.continuum.idl.api.schema.decorators.C3Decorator;

import java.util.*;

/**
 * Provides functionality to define an interface with a Continuum schema.
 * <p>
 * Created by navid on 2023-4-13.
 */
@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class ServiceDefinition {

    /**
     * The namespace this {@link ServiceDefinition} belongs to
     */
    private String namespace;

    /**
     * The name of this {@link ServiceDefinition}
     */
    private String name;

    /**
     * The list of Decorators that should be applied to this {@link ServiceDefinition}
     */
    @EqualsAndHashCode.Exclude
    protected List<C3Decorator> decorators = new ArrayList<>();

    /**
     * This defines {@link FunctionDefinition}'s for this {@link ServiceDefinition}
     * The key is the function name and the value is the schema that defines the function
     */
    @EqualsAndHashCode.Exclude
    @JsonDeserialize(as = LinkedList.class)
    private Set<FunctionDefinition> functions = new LinkedHashSet<>();

    /**
     * Stores the given value in the functions definitions for this schema
     * If a schema for the name already exists an error will be thrown
     * @param function {@link FunctionDefinition} defining the function
     * @return this
     */
    public ServiceDefinition addFunction(FunctionDefinition function){
        Validate.isTrue(!functions.contains(function), "ServiceDefinition already contains function "+function);
        functions.add(function);
        return this;
    }

    public ServiceDefinition addDecorator(C3Decorator decorator){
        decorators.add(decorator);
        return this;
    }

    /**
     * The URN is the namespace + "." + name
     * @return the urn for this {@link ServiceDefinition}
     */
    @JsonIgnore
    public String getUrn(){
        return namespace + "." + name;
    }

}
