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

import lombok.*;
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
@EqualsAndHashCode(callSuper = true)
public class ObjectC3Type extends C3Type {

    /**
     * The parent schema of this object definition
     * This is used to support inheritance
     */
    private ObjectC3Type parent = null;

    /**
     * The properties (key-value pairs) on an object are defined using the properties' keyword.
     * The value of properties is an object, where each key is the name of a property and each value is a Continuum schema used to validate that property.
     */
    @Singular
    private Map<String, C3Type> properties = new LinkedHashMap<>();

    public ObjectC3Type addProperty(String name, C3Type c3Type){
        Validate.isTrue(!properties.containsKey(name), "ObjectTypeDefinition already contains property for name "+name);
        properties.put(name, c3Type);
        return this;
    }

}
