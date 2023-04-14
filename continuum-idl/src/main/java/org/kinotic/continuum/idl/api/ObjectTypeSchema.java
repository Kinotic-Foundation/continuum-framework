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

package org.kinotic.continuum.idl.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.Validate;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Objects map property names to values. The property names are strings, and the values can be any Continuum Schema type.
 * Each of these pairs is conventionally referred to as a “property”.
 * <p>
 * Created by navid on 2019-06-11.
 */
@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class ObjectTypeSchema extends TypeSchema {

    /**
     * The parent schema of this object schema
     * This is used to support inheritance
     */
    private ObjectTypeSchema parent = null;

    /**
     * The properties (key-value pairs) on an object are defined using the properties' keyword.
     * The value of properties is an object, where each key is the name of a property and each value is a Continuum schema used to validate that property.
     */
    private Map<String, TypeSchema> properties = new LinkedHashMap<>();

    public ObjectTypeSchema addProperty(String name, TypeSchema typeSchema){
        Validate.isTrue(!properties.containsKey(name), "ObjectSchema already contains property for name "+name);
        properties.put(name, typeSchema);
        return this;
    }

}
