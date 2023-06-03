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

package org.kinotic.continuum.idl.api.schema;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.Validate;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Objects map property names to values. The property names are strings, and the values can be any Continuum Schema type.
 * Each of these pairs is conventionally referred to as a “property”.
 * NOTE: To {@link ObjectC3Type} are considered equivalent if they have the same namespace and name.
 *       Properties and decorators are not considered when determining equivalence.
 * <p>
 * Created by navid on 2019-06-11.
 */
@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class ObjectC3Type extends C3Type {

    /**
     * The namespace that this {@link ObjectC3Type} belongs to
     */
    private String namespace = null;

    /**
     * This is the name of the {@link ObjectC3Type} such as "Person", "Animal"
     */
    private String name = null;

    /**
     * The parent schema of this object definition
     * This is used to support inheritance
     */
    @EqualsAndHashCode.Exclude
    private ObjectC3Type parent = null;

    /**
     * The properties (key-value pairs) on an object are defined using the properties' keyword.
     * The value of properties is an object, where each key is the name of a property and each value is a Continuum schema used to validate that property.
     */
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Map<String, C3Type> properties = new LinkedHashMap<>();

    /**
     * Adds a property to this {@link ObjectC3Type}
     * @param name of the property
     * @param c3Type the type of the property
     * @return this {@link ObjectC3Type} for chaining
     */
    public ObjectC3Type addProperty(String name, C3Type c3Type){
        Validate.isTrue(!properties.containsKey(name), "ObjectC3Type already contains property for name "+name);
        properties.put(name, c3Type);
        return this;
    }

    /**
     * Gets the fully qualified name for this {@link ObjectC3Type} which is the namespace + "." + name
     * @return the fully qualified name for this {@link ObjectC3Type}
     */
    @JsonIgnore
    public String getQualifiedName(){
        return namespace + "." + name;
    }

}
