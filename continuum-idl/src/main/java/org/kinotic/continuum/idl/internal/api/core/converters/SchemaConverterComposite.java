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
import org.springframework.core.ResolvableType;
import org.springframework.util.Assert;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 * Created by navid on 2019-06-13.
 */
public class SchemaConverterComposite implements GenericTypeSchemaConverter {

    private final List<GenericTypeSchemaConverter> converters = new LinkedList<>();
    private final Map<String, SpecificTypeSchemaConverter> specificConverters = new LinkedHashMap<>();

    public SchemaConverterComposite addConverter(SchemaConverter converter){

        if(converter instanceof GenericTypeSchemaConverter){

            converters.add((GenericTypeSchemaConverter)converter);

        }else if(converter instanceof SpecificTypeSchemaConverter){

            SpecificTypeSchemaConverter specificConverter = (SpecificTypeSchemaConverter) converter;

            Class<?>[] specificTypes = specificConverter.supports();

            for(Class<?> type: specificTypes){
                Assert.notNull(type, "SpecificTypeJsonSchemaConverter classes returned from supports must not be null");
                Assert.isTrue(!specificConverters.containsKey(type.getName()),"SpecificTypeJsonSchemaConverter already exists for "+type.getName());

                specificConverters.put(type.getName(), specificConverter);
            }
        }else{
            throw new IllegalArgumentException("JsonSchemaConverter must implement GenericTypeJsonSchemaConverter or SpecificTypeJsonSchemaConverter");
        }
        return this;
    }

    public SchemaConverterComposite addConverters(SchemaConverter... converters){
        for(SchemaConverter converter: converters){
            addConverter(converter);
        }
        return this;
    }

    public SchemaConverterComposite addConverters(List<? extends SchemaConverter> converters){
        for(SchemaConverter converter: converters){
            addConverter(converter);
        }
        return this;
    }

    @Override
    public boolean supports(ResolvableType resolvableType) {
        Assert.notNull(resolvableType, "ResolvableType cannot be null");
        return selectConverter(resolvableType) != null;
    }

    @Override
    public Schema convert(ResolvableType resolvableType,
                          ConversionContext conversionContext) {
        Assert.notNull(resolvableType, "ResolvableType cannot be null");

        SchemaConverter converter = selectConverter(resolvableType);
        Assert.notNull(converter, "Unsupported Class no JsonSchemaConverter can be found for " + resolvableType);
        return converter.convert(resolvableType, conversionContext);
    }

    private SchemaConverter selectConverter(ResolvableType resolvableType){
        SchemaConverter ret = null;
        // check specific type then generic converters
        Class<?> rawClass = resolvableType.getRawClass();
        if (rawClass != null) {
            ret = specificConverters.get(rawClass.getName());
        }

        if (ret == null) {
            for (GenericTypeSchemaConverter converter : converters) {
                if (converter.supports(resolvableType)) {
                    ret = converter;
                    break;
                }
            }
        }
        return ret;
    }

}
