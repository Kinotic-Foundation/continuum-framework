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

package com.kinotic.continuum.api.jsonSchema;

import org.apache.commons.lang3.Validate;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Provides functionality to define a function with a Json schema. This is an extension to the standard spec.
 *
 *
 * Created by navid on 2019-06-20.
 */
public class FunctionJsonSchema extends JsonSchema {

    /**
     * This is the {@link JsonSchema} that defines the return type of this function.
     */
    private JsonSchema returnType = new VoidJsonSchema();

    /**
     * This map defines the {@link JsonSchema}'s that define the arguments for this function.
     * The Key is the argument name the value is the schema for the argument type.
     */
    private Map<String, JsonSchema> arguments = new LinkedHashMap<>();


    public FunctionJsonSchema addArgument(String name, JsonSchema schema){
        Validate.isTrue(!arguments.containsKey(name), "An argument already exists with the name "+name);
        arguments.put(name, schema);
        return this;
    }

    public Map<String, JsonSchema> getArguments() {
        return arguments;
    }

    public FunctionJsonSchema setArguments(Map<String, JsonSchema> arguments) {
        this.arguments = arguments;
        return this;
    }

    public JsonSchema getReturnType() {
        return returnType;
    }

    public FunctionJsonSchema setReturnType(JsonSchema returnType) {
        this.returnType = returnType;
        return this;
    }
}
