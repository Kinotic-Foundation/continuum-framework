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

package org.kinotic.continuum.idl.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.Validate;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Provides functionality to define a namespace with a Continuum schema.
 * A {@link NamespaceDefinition} is a collection of {@link ServiceDefinition}'s and {@link ObjectTypeDefinition}'s defined within a particular namespace.
 * <p>
 * Created by navid on 2023-4-13.
 */
@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class NamespaceDefinition {

    /**
     * This is all the objects defined for a given namespace
     */
    private Map<String, ObjectTypeDefinition> objectSchemas = new LinkedHashMap<>();

    /**
     * This is all the services defined for a given namespace
     */
    private Map<String, ServiceDefinition> serviceSchemas = new LinkedHashMap<>();


    public NamespaceDefinition addObjectSchema(String name, ObjectTypeDefinition schema){
        Validate.isTrue(!objectSchemas.containsKey(name), "This NamespaceSchema already contains an ObjectSchema for name "+name);
        objectSchemas.put(name, schema);
        return this;
    }

    public NamespaceDefinition addServiceSchema(String name, ServiceDefinition schema){
        Validate.isTrue(!serviceSchemas.containsKey(name), "This NamespaceSchema already contains an ServiceSchema for name "+name);
        serviceSchemas.put(name, schema);
        return this;
    }

}
