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

package org.kinotic.continuum.internal.api.jsonSchema.converters;

import org.kinotic.continuum.api.jsonSchema.JsonSchema;
import org.kinotic.continuum.api.jsonSchema.ObjectJsonSchema;
import org.kinotic.continuum.api.jsonSchema.ReferenceJsonSchema;
import org.springframework.core.ResolvableType;

import java.util.Map;

/**
 * Represents the current state of the conversion as well as providing a means for converters to convert dependent {@link JsonSchema}'s
 *
 * Created by navid on 2019-06-28.
 */
public interface ConversionContext {

    /**
     * Converts the given resolvable type into a fully qualified {@link JsonSchema}
     * This will not return a {@link ReferenceJsonSchema} under any circumstance
     * @param resolvableType to convert
     * @return the {@link JsonSchema} representing the {@link ResolvableType}
     */
    JsonSchema convert(ResolvableType resolvableType);

    /**
     * Converts the given resolvable type into a {@link JsonSchema} that is depended on by another {@link JsonSchema}
     * This will return a {@link ReferenceJsonSchema} when appropriate
     * @param resolvableType to convert
     * @return the {@link JsonSchema} representing the {@link ResolvableType}
     */
    JsonSchema convertDependency(ResolvableType resolvableType);

    /**
     * @return all of the {@link ObjectJsonSchema} known to this {@link ConversionContext}
     */
    Map<String, ObjectJsonSchema> getObjectSchemas();

}
