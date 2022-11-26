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

package org.kinotic.continuum.internal.api.jsonSchema.converters;

import org.kinotic.continuum.api.jsonSchema.JsonSchema;
import org.kinotic.continuum.api.jsonSchema.ObjectJsonSchema;
import org.kinotic.continuum.api.jsonSchema.ReferenceJsonSchema;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ResolvableType;
import org.springframework.util.Assert;

import java.util.*;

/**
 *
 * Created by navid on 2019-07-01.
 */
public class DefaultConversionContext implements ConversionContext {

    private static final Logger log = LoggerFactory.getLogger(DefaultConversionContext.class);

    private JsonSchemaConverter jsonSchemaConverter;

    private Deque<ResolvableType> circularReferenceCheckStack = new ArrayDeque<>();

    private Deque<ResolvableType> errorStack = new ArrayDeque<>();

    private Map<String, JsonSchema> schemaCache = new HashMap<>();

    private Map<String, ObjectJsonSchema> objectSchemaMap = new LinkedHashMap<>();

    /**
     * Creates a new {@link ConversionContext}
     * @param jsonSchemaConverter the converter to use to be used for conversion. Typically this will be a {@link JsonSchemaConverterComposite}
     */
    public DefaultConversionContext(JsonSchemaConverter jsonSchemaConverter) {
        this.jsonSchemaConverter = jsonSchemaConverter;
    }

    @Override
    public JsonSchema convert(ResolvableType resolvableType) {

        if(circularReferenceCheckStack.contains(resolvableType)){
            IllegalStateException ise = new IllegalStateException("Circular reference detected for "+resolvableType);
            logException(ise);
            throw ise;
        }
        JsonSchema ret;
        try {

            circularReferenceCheckStack.addFirst(resolvableType);

            String key = resolvableType.toString();
            if (schemaCache.containsKey(key)) {
                ret = schemaCache.get(key);
            } else {
                ret = jsonSchemaConverter.convert(resolvableType, this);
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
    public JsonSchema convertDependency(ResolvableType resolvableType) {
        JsonSchema schema = convert(resolvableType);
        if(schema instanceof ObjectJsonSchema){
            Class<?> rawClass = resolvableType.getRawClass();
            Assert.notNull(rawClass, "Cannot determine name for ObjectJsonSchema");
            String name = rawClass.getName();
            objectSchemaMap.put(name, (ObjectJsonSchema)schema);
            schema = new ReferenceJsonSchema(name);
        }
        return schema;
    }

    public Map<String, ObjectJsonSchema> getObjectSchemas() {
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
