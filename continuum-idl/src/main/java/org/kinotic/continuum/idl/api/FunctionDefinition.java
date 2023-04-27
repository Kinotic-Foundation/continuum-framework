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
 * Provides functionality to define a function with a Continuum schema.
 * Created by navid on 2023-4-13
 */
@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class FunctionDefinition {

    /**
     * This is the {@link C3Type} that defines the return type of this function.
     */
    private C3Type returnType = new VoidC3Type();

    /**
     * This map defines the {@link C3Type}'s that define the arguments for this function.
     * The Key is the argument name the value is the schema for the argument type.
     * Argument names must be unique.
     */
    private Map<String, C3Type> arguments = new LinkedHashMap<>();


    public FunctionDefinition addArgument(String name, C3Type c3Type){
        Validate.isTrue(!arguments.containsKey(name), "An argument already exists with the name "+name);
        arguments.put(name, c3Type);
        return this;
    }

}
