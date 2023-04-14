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

package org.kinotic.continuum.idl.internal.api.converters.jdk;

import org.apache.commons.lang3.Validate;
import org.kinotic.continuum.idl.api.MapTypeSchema;
import org.kinotic.continuum.idl.api.TypeSchema;
import org.kinotic.continuum.idl.internal.api.converters.ConversionContext;
import org.kinotic.continuum.idl.internal.api.converters.GenericTypeSchemaConverter;
import org.springframework.core.ResolvableType;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 *
 * Created by navid on 2019-07-31
 */
@Component
public class MapSchemaConverter implements GenericTypeSchemaConverter {

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
    public TypeSchema convert(ResolvableType resolvableType, ConversionContext conversionContext) {

        ResolvableType keyType = resolvableType.getGeneric(0);
        Validate.notNull(keyType, "Map Key type must not be null for "+ resolvableType);
        ResolvableType valueType = resolvableType.getGeneric(1);
        Validate.notNull(valueType, "Map Value type must not be null for "+ resolvableType);

        return new MapTypeSchema(conversionContext.convertDependency(keyType), conversionContext.convertDependency(valueType));
    }
}
