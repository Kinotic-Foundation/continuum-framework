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

package com.kinotic.continuum.internal.api.jsonSchema;

import com.kinotic.continuum.api.annotations.Name;
import com.kinotic.continuum.api.jsonSchema.*;
import com.kinotic.continuum.internal.api.jsonSchema.converters.ConversionContext;
import com.kinotic.continuum.internal.api.jsonSchema.converters.DefaultConversionContext;
import com.kinotic.continuum.internal.api.jsonSchema.converters.GenericTypeJsonSchemaConverter;
import org.springframework.core.MethodParameter;
import org.springframework.core.ResolvableType;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

/**
 * Provides the ability to create {@link JsonSchema}'s
 *
 *
 * Created by navid on 2019-06-13.
 */
@Component
public class DefaultJsonSchemaFactory implements JsonSchemaFactory {

    private final GenericTypeJsonSchemaConverter jsonSchemaConverter;

    public DefaultJsonSchemaFactory(GenericTypeJsonSchemaConverter jsonSchemaConverter) {
        this.jsonSchemaConverter = jsonSchemaConverter;
    }

    @Override
    public JsonSchema createForPojo(Class<?> clazz) {
        DefaultConversionContext conversionContext = new DefaultConversionContext(jsonSchemaConverter);
        return this.createForPojo(clazz, conversionContext);
    }

    private JsonSchema createForPojo(Class<?> clazz, ConversionContext conversionContext) {
        Assert.notNull(clazz, "Class cannot be null");
        Assert.notNull(conversionContext, "ConversionContext cannot be null");

        JsonSchema ret;
        ResolvableType resolvableType = ResolvableType.forClass(clazz);
        if(jsonSchemaConverter.supports(resolvableType)){

            ret = jsonSchemaConverter.convert(resolvableType, conversionContext);

        }else{
            throw new IllegalArgumentException("No JsonSchemaConverter can be found for "+ clazz.getName());
        }
        return ret;
    }

    @Override
    public NamespaceJsonSchema createForService(Class<?> clazz) {
        DefaultConversionContext conversionContext = new DefaultConversionContext(jsonSchemaConverter);
        return this.createForService(clazz, conversionContext);
    }

    private NamespaceJsonSchema createForService(Class<?> clazz, ConversionContext conversionContext) {
        Assert.notNull(clazz, "Class cannot be null");
        Assert.notNull(conversionContext, "ConversionContext cannot be null");


        ServiceJsonSchema serviceJsonSchema = new ServiceJsonSchema();

        ReflectionUtils.doWithMethods(clazz, method -> {
            // TODO: make this work properly when an interface defines generics that the implementor will define in implementation, This would require a interface class and a target class above to work correctly

            FunctionJsonSchema functionJsonSchema = new FunctionJsonSchema();
            functionJsonSchema.setReturnType(conversionContext.convertDependency(ResolvableType.forMethodReturnType(method)));

            for (int i = 0; i < method.getParameterCount(); i++) {

                MethodParameter methodParameter = new MethodParameter(method, i);

                JsonSchema schema = conversionContext.convertDependency(ResolvableType.forMethodParameter(methodParameter));

                functionJsonSchema.addArgument(getName(methodParameter), schema);
            }

            serviceJsonSchema.addFunction(method.getName(), functionJsonSchema);

        }, ReflectionUtils.USER_DECLARED_METHODS);

        NamespaceJsonSchema ret = new NamespaceJsonSchema();
        ret.setObjectSchemas(conversionContext.getObjectSchemas());
        ret.addServiceSchema(clazz.getName(), serviceJsonSchema);

        return ret;
    }


    private String getName(MethodParameter methodParameter){
        String ret;
        Name nameAnnotation = methodParameter.getParameterAnnotation(Name.class);
        if(nameAnnotation != null){
            ret = nameAnnotation.value();
        }else{
            ret = methodParameter.getParameter().getName();
        }
        return ret;
    }

}
