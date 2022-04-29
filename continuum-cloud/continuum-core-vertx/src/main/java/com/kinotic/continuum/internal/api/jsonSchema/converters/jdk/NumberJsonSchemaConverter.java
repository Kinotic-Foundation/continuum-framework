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

package com.kinotic.continuum.internal.api.jsonSchema.converters.jdk;

import com.kinotic.continuum.api.jsonSchema.JsonSchema;
import com.kinotic.continuum.api.jsonSchema.NumberJsonSchema;
import com.kinotic.continuum.internal.api.jsonSchema.converters.ConversionContext;
import com.kinotic.continuum.internal.api.jsonSchema.converters.SpecificTypeJsonSchemaConverter;
import org.springframework.core.ResolvableType;
import org.springframework.stereotype.Component;

/**
 *
 * Created by navid on 2019-06-14.
 */
@Component
public class NumberJsonSchemaConverter implements SpecificTypeJsonSchemaConverter {

    private static final Class<?>[] supports = {int.class, Integer.class, long.class, Long.class, double.class, Double.class, float.class, Float.class};

    @Override
    public Class<?>[] supports() {
        return supports;
    }

    @Override
    public JsonSchema convert(ResolvableType resolvableType,
                              ConversionContext conversionContext) {
        // TODO: set max min ect for NumberJsonSchema
        return new NumberJsonSchema();
    }

}
