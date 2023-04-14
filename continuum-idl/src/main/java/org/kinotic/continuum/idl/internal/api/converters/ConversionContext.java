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

package org.kinotic.continuum.idl.internal.api.converters;

import org.kinotic.continuum.idl.api.TypeSchema;
import org.kinotic.continuum.idl.api.ObjectTypeSchema;
import org.kinotic.continuum.idl.api.ReferenceTypeSchema;
import org.springframework.core.ResolvableType;

import java.util.Map;

/**
 * Represents the current state of the conversion as well as providing a means for converters to convert dependent {@link TypeSchema}'s
 * <p>
 * Created by navid on 2019-06-28.
 */
public interface ConversionContext {

    /**
     * Converts the given resolvable type into a fully qualified {@link TypeSchema}
     * This will not return a {@link ReferenceTypeSchema} under any circumstance
     * @param resolvableType to convert
     * @return the {@link TypeSchema} representing the {@link ResolvableType}
     */
    TypeSchema convert(ResolvableType resolvableType);

    /**
     * Converts the given resolvable type into a {@link TypeSchema} that is depended on by another {@link TypeSchema}
     * This will return a {@link ReferenceTypeSchema} when appropriate
     * @param resolvableType to convert
     * @return the {@link TypeSchema} representing the {@link ResolvableType}
     */
    TypeSchema convertDependency(ResolvableType resolvableType);

    /**
     * @return all of the {@link ObjectTypeSchema} known to this {@link ConversionContext}
     */
    Map<String, ObjectTypeSchema> getObjectSchemas();

}
