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
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.*;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.Validate;
import org.kinotic.continuum.idl.api.schema.decorators.C3Decorator;

import java.util.LinkedList;
import java.util.List;

/**
 * ObjectC3Type is used to define a complex object type in the Continuum IDL.
 * Properties are defined with {@link PropertyDefinition}s
 * The context for equality here is the namespace and name.
 * Given no two object types can have the same namespace and name this is the only context needed for equality.
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
    @EqualsAndHashCode.Exclude
    @JsonDeserialize(as = LinkedList.class)
    private LinkedList<PropertyDefinition> properties = new LinkedList<>();

    /**
     * Adds a property to this {@link ObjectC3Type}
     * @param name of the property
     * @param c3Type the type of the property
     * @return this {@link ObjectC3Type} for chaining
     */
    public ObjectC3Type addProperty(String name, C3Type c3Type){
        PropertyDefinition property = new PropertyDefinition().setName(name).setType(c3Type);
        return addProperty(property);
    }

    /**
     * Adds a property to this {@link ObjectC3Type}
     * @param name of the property
     * @param c3Type the type of the property
     * @param decorators the decorators to apply to the property
     * @return this {@link ObjectC3Type} for chaining
     */
    public ObjectC3Type addProperty(String name, C3Type c3Type, List<C3Decorator> decorators){
        PropertyDefinition property = new PropertyDefinition().setName(name).setType(c3Type).setDecorators(decorators);
        return addProperty(property);
    }

    /**
     * Adds a property to this {@link ObjectC3Type}
     * @param propertyDefinition to add to this {@link ObjectC3Type}
     * @return this {@link ObjectC3Type} for chaining
     */
    public ObjectC3Type addProperty(PropertyDefinition propertyDefinition){
        Validate.isTrue(!properties.contains(propertyDefinition), "ObjectC3Type already contains property "+propertyDefinition.getName());
        properties.add(propertyDefinition);
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
