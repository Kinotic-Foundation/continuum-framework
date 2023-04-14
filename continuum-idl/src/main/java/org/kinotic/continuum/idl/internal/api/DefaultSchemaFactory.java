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

package org.kinotic.continuum.idl.internal.api;

import org.kinotic.continuum.api.annotations.Name;
import org.kinotic.continuum.idl.api.*;
import org.kinotic.continuum.idl.internal.api.converters.ConversionContext;
import org.kinotic.continuum.idl.internal.api.converters.DefaultConversionContext;
import org.kinotic.continuum.idl.internal.api.converters.GenericTypeSchemaConverter;
import org.springframework.core.MethodParameter;
import org.springframework.core.ResolvableType;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

/**
 * Provides the ability to create {@link TypeSchema}'s
 *
 *
 * Created by navid on 2019-06-13.
 */
@Component
public class DefaultSchemaFactory implements SchemaFactory {

    private final GenericTypeSchemaConverter schemaConverter;

    public DefaultSchemaFactory(GenericTypeSchemaConverter schemaConverter) {
        this.schemaConverter = schemaConverter;
    }

    @Override
    public TypeSchema createForClass(Class<?> clazz) {
        DefaultConversionContext conversionContext = new DefaultConversionContext(schemaConverter);
        return this.createForPojo(clazz, conversionContext);
    }

    private TypeSchema createForPojo(Class<?> clazz, ConversionContext conversionContext) {
        Assert.notNull(clazz, "Class cannot be null");
        Assert.notNull(conversionContext, "ConversionContext cannot be null");

        TypeSchema ret;
        ResolvableType resolvableType = ResolvableType.forClass(clazz);
        if(schemaConverter.supports(resolvableType)){

            ret = schemaConverter.convert(resolvableType, conversionContext);

        }else{
            throw new IllegalArgumentException("No schemaConverter can be found for "+ clazz.getName());
        }
        return ret;
    }

    @Override
    public NamespaceSchema createForService(Class<?> clazz) {
        DefaultConversionContext conversionContext = new DefaultConversionContext(schemaConverter);
        return this.createForService(clazz, conversionContext);
    }

    private NamespaceSchema createForService(Class<?> clazz, ConversionContext conversionContext) {
        Assert.notNull(clazz, "Class cannot be null");
        Assert.notNull(conversionContext, "ConversionContext cannot be null");


        ServiceSchema serviceSchema = new ServiceSchema();

        ReflectionUtils.doWithMethods(clazz, method -> {
            // TODO: make this work properly when an interface defines generics that the implementor will define in implementation, This would require an interface class and a target class above to work correctly

            FunctionSchema functionSchema = new FunctionSchema();
            functionSchema.setReturnType(conversionContext.convertDependency(ResolvableType.forMethodReturnType(method)));

            for (int i = 0; i < method.getParameterCount(); i++) {

                MethodParameter methodParameter = new MethodParameter(method, i);

                TypeSchema typeSchema = conversionContext.convertDependency(ResolvableType.forMethodParameter(methodParameter));

                functionSchema.addArgument(getName(methodParameter), typeSchema);
            }

            serviceSchema.addFunction(method.getName(), functionSchema);

        }, ReflectionUtils.USER_DECLARED_METHODS);

        NamespaceSchema ret = new NamespaceSchema();
        ret.setObjectSchemas(conversionContext.getObjectSchemas());
        ret.addServiceSchema(clazz.getName(), serviceSchema);

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
