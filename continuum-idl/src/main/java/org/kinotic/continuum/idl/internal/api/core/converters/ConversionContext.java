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

package org.kinotic.continuum.idl.internal.api.core.converters;

import org.kinotic.continuum.idl.api.Schema;
import org.kinotic.continuum.idl.api.ObjectSchema;
import org.kinotic.continuum.idl.api.ReferenceSchema;
import org.springframework.core.ResolvableType;

import java.util.Map;

/**
 * Represents the current state of the conversion as well as providing a means for converters to convert dependent {@link Schema}'s
 * <p>
 * Created by navid on 2019-06-28.
 */
public interface ConversionContext {

    /**
     * Converts the given resolvable type into a fully qualified {@link Schema}
     * This will not return a {@link ReferenceSchema} under any circumstance
     * @param resolvableType to convert
     * @return the {@link Schema} representing the {@link ResolvableType}
     */
    Schema convert(ResolvableType resolvableType);

    /**
     * Converts the given resolvable type into a {@link Schema} that is depended on by another {@link Schema}
     * This will return a {@link ReferenceSchema} when appropriate
     * @param resolvableType to convert
     * @return the {@link Schema} representing the {@link ResolvableType}
     */
    Schema convertDependency(ResolvableType resolvableType);

    /**
     * @return all of the {@link ObjectSchema} known to this {@link ConversionContext}
     */
    Map<String, ObjectSchema> getObjectSchemas();

}
