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

import org.kinotic.continuum.idl.api.C3Type;
import org.springframework.core.ResolvableType;
import org.springframework.util.Assert;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Composes all the {@link TypeConverter}'s into a single component for use by the {@link org.kinotic.continuum.idl.internal.api.DefaultSchemaFactory}
 * Created by navid on 2019-06-13.
 */
public class TypeConverterComposite implements GenericTypeConverter {

    private final List<GenericTypeConverter> converters = new LinkedList<>();
    private final Map<String, SpecificTypeConverter> specificConverters = new LinkedHashMap<>();

    public TypeConverterComposite addConverter(TypeConverter converter){

        if(converter instanceof GenericTypeConverter){

            converters.add((GenericTypeConverter)converter);

        }else if(converter instanceof SpecificTypeConverter){

            SpecificTypeConverter specificConverter = (SpecificTypeConverter) converter;

            Class<?>[] specificTypes = specificConverter.supports();

            for(Class<?> type: specificTypes){
                Assert.notNull(type, "SpecificTypeSchemaConverter classes returned from supports must not be null");
                Assert.isTrue(!specificConverters.containsKey(type.getName()),"SpecificTypeSchemaConverter already exists for "+type.getName());

                specificConverters.put(type.getName(), specificConverter);
            }
        }else{
            throw new IllegalArgumentException("schemaConverter must implement GenericTypeSchemaConverter or SpecificTypeSchemaConverter");
        }
        return this;
    }

    public TypeConverterComposite addConverters(TypeConverter... converters){
        for(TypeConverter converter: converters){
            addConverter(converter);
        }
        return this;
    }

    public TypeConverterComposite addConverters(List<? extends TypeConverter> converters){
        for(TypeConverter converter: converters){
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
    public C3Type convert(ResolvableType resolvableType,
                          ConversionContext conversionContext) {
        Assert.notNull(resolvableType, "ResolvableType cannot be null");

        TypeConverter converter = selectConverter(resolvableType);
        Assert.notNull(converter, "Unsupported Class no schemaConverter can be found for " + resolvableType);
        return converter.convert(resolvableType, conversionContext);
    }

    private TypeConverter selectConverter(ResolvableType resolvableType){
        TypeConverter ret = null;
        // check specific type then generic converters
        Class<?> rawClass = resolvableType.getRawClass();
        if (rawClass != null) {
            ret = specificConverters.get(rawClass.getName());
        }

        if (ret == null) {
            for (GenericTypeConverter converter : converters) {
                if (converter.supports(resolvableType)) {
                    ret = converter;
                    break;
                }
            }
        }
        return ret;
    }

}
