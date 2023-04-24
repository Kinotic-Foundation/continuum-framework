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

import org.kinotic.continuum.idl.api.TypeDefinition;
import org.kinotic.continuum.idl.api.ObjectTypeDefinition;
import org.kinotic.continuum.idl.api.ReferenceTypeDefinition;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ResolvableType;
import org.springframework.util.Assert;

import java.util.*;

/**
 * Default implementation of {@link ConversionContext}
 * Created by navid on 2019-07-01.
 */
public class DefaultConversionContext implements ConversionContext {

    private static final Logger log = LoggerFactory.getLogger(DefaultConversionContext.class);

    private final TypeConverter typeConverter;

    private final Deque<ResolvableType> circularReferenceCheckStack = new ArrayDeque<>();

    private final Deque<ResolvableType> errorStack = new ArrayDeque<>();

    private final Map<String, TypeDefinition> schemaCache = new HashMap<>();

    private final Map<String, ObjectTypeDefinition> objectSchemaMap = new LinkedHashMap<>();

    /**
     * Creates a new {@link ConversionContext}
     * @param typeConverter the converter to use to be used for conversion. Typically, this will be a {@link TypeConverterComposite}
     */
    public DefaultConversionContext(TypeConverter typeConverter) {
        this.typeConverter = typeConverter;
    }

    @Override
    public TypeDefinition convert(ResolvableType resolvableType) {

        if(circularReferenceCheckStack.contains(resolvableType)){
            IllegalStateException ise = new IllegalStateException("Circular reference detected for "+resolvableType);
            logException(ise);
            throw ise;
        }
        TypeDefinition ret;
        try {

            circularReferenceCheckStack.addFirst(resolvableType);

            String key = resolvableType.toString();
            if (schemaCache.containsKey(key)) {
                ret = schemaCache.get(key);
            } else {
                ret = typeConverter.convert(resolvableType, this);
                schemaCache.put(key, ret);
            }

        } catch (Exception e){
            logException(e);
            throw e;
        } finally {
            circularReferenceCheckStack.removeFirst();
        }
        return ret;
    }

    @Override
    public TypeDefinition convertDependency(ResolvableType resolvableType) {
        TypeDefinition typeDefinition = convert(resolvableType);
        if(typeDefinition instanceof ObjectTypeDefinition){
            Class<?> rawClass = resolvableType.getRawClass();
            Assert.notNull(rawClass, "Cannot determine name for ObjectSchema");
            String name = rawClass.getName();
            objectSchemaMap.put(name, (ObjectTypeDefinition) typeDefinition);
            typeDefinition = new ReferenceTypeDefinition(name);
        }
        return typeDefinition;
    }

    public Map<String, ObjectTypeDefinition> getObjectSchemas() {
        return objectSchemaMap;
    }

    /**
     * Log an exception when appropriate dealing with only logging once even when recursion has occurred
     * @param e to log
     */
    private void logException(Exception e){
        if(log.isDebugEnabled() || log.isTraceEnabled()){
            // This indicates this is the first time logException has been called for this context.
            if(errorStack.isEmpty()){
                // We loop vs add all to keep stack intact
                for(ResolvableType resolvableType: circularReferenceCheckStack){
                    errorStack.addFirst(resolvableType);
                }
            }
            if(circularReferenceCheckStack.size() == 1) { // we are at the top of the stack during recursion
                StringBuilder sb = new StringBuilder("Error occurred during conversion.\n" + e.getMessage() + "\n");
                int objectCount = 1;
                for (ResolvableType resolvableType : errorStack) {
                    sb.append(StringUtils.leftPad("", objectCount, '\t'));
                    sb.append("- ");
                    sb.append(resolvableType.toString());
                    sb.append("\n");
                    objectCount++;
                }
                log.debug(sb.toString());
                errorStack.clear(); // we have printed reset
            }
        }
    }

}
