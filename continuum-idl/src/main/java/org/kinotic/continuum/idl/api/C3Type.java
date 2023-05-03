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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.*;
import lombok.experimental.Accessors;
import org.kinotic.continuum.idl.api.decorators.C3Decorator;

import java.util.List;
import java.util.Map;

/**
 * This is the base class for all type schemas.
 * It can be used to create {@link C3Type} from use within a Continuum IDL.
 * <p>
 * Created by navid on 2023-4-13.
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = ArrayC3Type.class, name = "array"),
        @JsonSubTypes.Type(value = BooleanC3Type.class, name = "boolean"),
        @JsonSubTypes.Type(value = ByteC3Type.class, name = "byte"),
        @JsonSubTypes.Type(value = CharC3Type.class, name = "char"),
        @JsonSubTypes.Type(value = DateC3Type.class, name = "date"),
        @JsonSubTypes.Type(value = DoubleC3Type.class, name = "double"),
        @JsonSubTypes.Type(value = EnumC3Type.class, name = "enum"),
        @JsonSubTypes.Type(value = FloatC3Type.class, name = "float"),
        @JsonSubTypes.Type(value = IntC3Type.class, name = "int"),
        @JsonSubTypes.Type(value = LongC3Type.class, name = "long"),
        @JsonSubTypes.Type(value = MapC3Type.class, name = "map"),
        @JsonSubTypes.Type(value = ObjectC3Type.class, name = "object"),
        @JsonSubTypes.Type(value = ReferenceC3Type.class, name = "ref"),
        @JsonSubTypes.Type(value = ShortC3Type.class, name = "short"),
        @JsonSubTypes.Type(value = StringC3Type.class, name = "string"),
        @JsonSubTypes.Type(value = UnionC3Type.class, name = "union"),
        @JsonSubTypes.Type(value = VoidC3Type.class, name = "void")
})
@JsonInclude(JsonInclude.Include.NON_EMPTY) // do not include any empty or null fields
@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public abstract class C3Type {

    /**
     * The metadata keyword is legal on any schema, The objects provided must be serializable to JSON.
     * Usually, metadata is for putting things like descriptions or hints for code generators, or other things tools can use.
     */
    private Map<String, ?> metadata;

    /**
     * The list of Decorators that should be applied to this type
     */
    private List<C3Decorator> decorators;

    /**
     * Checks if this type contains a {@link C3Decorator} of the given subclass
     * @param clazz to see if this type has
     * @return true if the {@link C3Decorator} is present false if not
     */
    public boolean containsDecorator(Class<? extends C3Decorator> clazz){
        return findDecorator(clazz) != null;
    }

    /**
     * Checks if this type has any {@link C3Decorator}
     * @return true if any {@link C3Decorator}s are present false if not
     */
    public boolean hasDecorators(){
        return decorators != null && !decorators.isEmpty();
    }

    /**
     * Finds the first {@link C3Decorator} of the given subclass or null if none are found
     * @param clazz to find the {@link C3Decorator} for
     * @return the {@link C3Decorator} or null if none are found
     */
    public C3Decorator findDecorator(Class<? extends C3Decorator> clazz){
        C3Decorator ret = null;
        if(decorators != null){
            for (C3Decorator decorator : decorators){
                if(clazz.isAssignableFrom(decorator.getClass())){
                    ret = decorator;
                    break;
                }
            }
        }
        return ret;
    }

}
