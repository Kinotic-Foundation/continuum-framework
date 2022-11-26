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

package org.kinotic.continuum.api.jsonSchema;

import org.apache.commons.lang3.Validate;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Provides functionality to define an namespace with a Json schema. This is an extension to the standard spec.
 * A {@link NamespaceJsonSchema} is a collection of {@link ServiceJsonSchema}'s and {@link ObjectJsonSchema}'s defined within a particular namespace.
 *
 *
 * Created by navid on 2019-06-20.
 */
public class NamespaceJsonSchema extends JsonSchema {

    /**
     * This is all of the objects defined for a given namespace
     */
    private Map<String, ObjectJsonSchema> objectSchemas = new LinkedHashMap<>();

    /**
     * This is all of the services defined for a given namespace
     */
    private Map<String, ServiceJsonSchema> serviceSchemas = new LinkedHashMap<>();


    public NamespaceJsonSchema addObjectSchema(String name, ObjectJsonSchema schema){
        Validate.isTrue(!objectSchemas.containsKey(name), "This NamespaceJsonSchema already contains an ObjectJsonSchema for name "+name);
        objectSchemas.put(name, schema);
        return this;
    }

    public Map<String, ObjectJsonSchema> getObjectSchemas() {
        return objectSchemas;
    }

    public NamespaceJsonSchema setObjectSchemas(Map<String, ObjectJsonSchema> objectSchemas) {
        this.objectSchemas = objectSchemas;
        return this;
    }

    public NamespaceJsonSchema addServiceSchema(String name, ServiceJsonSchema schema){
        Validate.isTrue(!serviceSchemas.containsKey(name), "This NamespaceJsonSchema already contains an ServiceJsonSchema for name "+name);
        serviceSchemas.put(name, schema);
        return this;
    }

    public Map<String, ServiceJsonSchema> getServiceSchemas() {
        return serviceSchemas;
    }

    public NamespaceJsonSchema setServiceSchemas(Map<String, ServiceJsonSchema> serviceSchemas) {
        this.serviceSchemas = serviceSchemas;
        return this;
    }
}
