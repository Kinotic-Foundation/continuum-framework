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
import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import org.springframework.beans.BeanUtils;
import org.springframework.core.ResolvableType;
import org.springframework.util.Assert;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

/**
 * Converts all generic POJO's
 * <p>
 * Created by navid on 2019-06-14.
 */
public class PojoSchemaConverter implements GenericTypeSchemaConverter {

    @Override
    public boolean supports(ResolvableType resolvableType) {
        Class<?> rawClass = resolvableType.getRawClass();

        return rawClass != null
                && !rawClass.getPackage().getName().startsWith("java")
                && !rawClass.getPackage().getName().startsWith("javax")
                && !rawClass.getPackage().getName().startsWith("jdk")
                && !rawClass.getPackage().getName().startsWith("sun")
                && !rawClass.getPackage().getName().startsWith("org.codehaus.groovy")
                && Object.class.isAssignableFrom(rawClass);
    }

    @Override
    public TypeSchema convert(ResolvableType resolvableType,
                              ConversionContext conversionContext) {

        ObjectTypeSchema ret = new ObjectTypeSchema();

        Class<?> rawClass = resolvableType.getRawClass();
        Assert.notNull(rawClass, "Raw class could not be found for ResolvableType");

        PropertyDescriptor[] descriptors = BeanUtils.getPropertyDescriptors(rawClass);

        for(PropertyDescriptor descriptor: descriptors){

            if(!ignorePropertyDescriptor(descriptor)) {

                ResolvableType returnTypeResolvableType = ResolvableType.forMethodReturnType(descriptor.getReadMethod());

                TypeSchema fieldTypeSchema = conversionContext.convertDependency(returnTypeResolvableType);

                ret.addProperty(descriptor.getName(), fieldTypeSchema);
            }
        }
        return ret;
    }

    private boolean ignorePropertyDescriptor(PropertyDescriptor descriptor){
        boolean ret = false;
        if(descriptor.getReadMethod() == null
            || isInternalObjectMethod(descriptor.getReadMethod())){
            ret = true;
        }
        return ret;
    }

    private boolean isInternalObjectMethod(Method method){
        boolean ret = false;
        Class<?> declaringClass = method.getDeclaringClass();
        if(declaringClass.isAssignableFrom(Object.class)
           || declaringClass.isAssignableFrom(GroovyObject.class)
           || declaringClass.isAssignableFrom(MetaClass.class)){
            ret = true;
        }
        return ret;
    }

}
