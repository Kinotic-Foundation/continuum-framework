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

import lombok.*;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.Validate;

import java.util.LinkedHashMap;
import java.util.Map;

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
public class ServiceDefinition {

    /**
     * This defines {@link FunctionDefinition}'s for this {@link ServiceDefinition}
     * The key is the function name and the value is the schema that defines the function
     */
    private Map<String, FunctionDefinition> functions = new LinkedHashMap<>();

    /**
     * Stores the given value in the functions definitions for this schema
     * If a schema for the name already exists an error will be thrown
     * @param name the name of the function
     * @param schema {@link FunctionDefinition} defining the function
     * @return this
     */
    public ServiceDefinition addFunction(String name, FunctionDefinition schema){
        Validate.isTrue(!functions.containsKey(name), "InterfaceJsonSchema already contains function for name "+name);
        functions.put(name, schema);
        return this;
    }
}
