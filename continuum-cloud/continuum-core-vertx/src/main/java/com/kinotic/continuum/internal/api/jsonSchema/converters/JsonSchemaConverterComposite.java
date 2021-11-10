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

package com.kinotic.continuum.internal.api.jsonSchema.converters;

import com.kinotic.continuum.api.jsonSchema.JsonSchema;
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
public class JsonSchemaConverterComposite implements GenericTypeJsonSchemaConverter {

    private final List<GenericTypeJsonSchemaConverter> converters = new LinkedList<>();
    private final Map<String, SpecificTypeJsonSchemaConverter> specificConverters = new LinkedHashMap<>();

    public JsonSchemaConverterComposite addConverter(JsonSchemaConverter converter){

        if(converter instanceof GenericTypeJsonSchemaConverter){

            converters.add((GenericTypeJsonSchemaConverter)converter);

        }else if(converter instanceof SpecificTypeJsonSchemaConverter){

            SpecificTypeJsonSchemaConverter specificConverter = (SpecificTypeJsonSchemaConverter) converter;

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

    public JsonSchemaConverterComposite addConverters(JsonSchemaConverter... converters){
        for(JsonSchemaConverter converter: converters){
            addConverter(converter);
        }
        return this;
    }

    public JsonSchemaConverterComposite addConverters(List<? extends JsonSchemaConverter> converters){
        for(JsonSchemaConverter converter: converters){
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
    public JsonSchema convert(ResolvableType resolvableType,
                              ConversionContext conversionContext) {
        Assert.notNull(resolvableType, "ResolvableType cannot be null");

        JsonSchemaConverter converter = selectConverter(resolvableType);
        Assert.notNull(converter, "Unsupported Class no JsonSchemaConverter can be found for " + resolvableType);
        return converter.convert(resolvableType, conversionContext);
    }

    private JsonSchemaConverter selectConverter(ResolvableType resolvableType){
        JsonSchemaConverter ret = null;
        // check specific type then generic converters
        Class<?> rawClass = resolvableType.getRawClass();
        if (rawClass != null) {
            ret = specificConverters.get(rawClass.getName());
        }

        if (ret == null) {
            for (GenericTypeJsonSchemaConverter converter : converters) {
                if (converter.supports(resolvableType)) {
                    ret = converter;
                    break;
                }
            }
        }
        return ret;
    }

}
