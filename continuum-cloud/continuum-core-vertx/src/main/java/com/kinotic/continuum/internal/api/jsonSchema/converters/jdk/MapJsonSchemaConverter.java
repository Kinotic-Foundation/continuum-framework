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
import com.kinotic.continuum.api.jsonSchema.MapJsonSchema;
import com.kinotic.continuum.internal.api.jsonSchema.converters.ConversionContext;
import com.kinotic.continuum.internal.api.jsonSchema.converters.GenericTypeJsonSchemaConverter;
import org.springframework.core.ResolvableType;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Map;

/**
 *
 * Created by navid on 2019-07-31
 */
@Component
public class MapJsonSchemaConverter implements GenericTypeJsonSchemaConverter {

    @Override
    public boolean supports(ResolvableType resolvableType) {
        boolean ret = false;

        ResolvableType collectionResolvableType = resolvableType.as(Map.class);
        if(!collectionResolvableType.equals(ResolvableType.NONE)){
            ret = true;
        }
        return ret;
    }

    @Override
    public JsonSchema convert(ResolvableType resolvableType, ConversionContext conversionContext) {

        ResolvableType keyType = resolvableType.getGeneric(0);
        Assert.notNull(keyType, "Map Key type must not be null for "+resolvableType.toString());
        ResolvableType valueType = resolvableType.getGeneric(1);
        Assert.notNull(valueType, "Map Value type must not be null for "+resolvableType.toString());

        return new MapJsonSchema(conversionContext.convertDependency(keyType), conversionContext.convertDependency(valueType));
    }
}
