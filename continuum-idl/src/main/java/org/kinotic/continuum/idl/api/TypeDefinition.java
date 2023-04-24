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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.kinotic.continuum.idl.api.decorators.DecoratorDefinition;

import java.util.List;
import java.util.Map;

/**
 * This is the base class for all type schemas.
 * It can be used to create {@link TypeDefinition} from use within a Continuum IDL.
 * <p>
 * Created by navid on 2023-4-13.
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = ArrayTypeDefinition.class, name = "array"),
        @JsonSubTypes.Type(value = BooleanTypeDefinition.class, name = "boolean"),
        @JsonSubTypes.Type(value = ByteTypeDefinition.class, name = "byte"),
        @JsonSubTypes.Type(value = CharTypeDefinition.class, name = "char"),
        @JsonSubTypes.Type(value = DateTypeDefinition.class, name = "date"),
        @JsonSubTypes.Type(value = DoubleTypeDefinition.class, name = "double"),
        @JsonSubTypes.Type(value = EnumTypeDefinition.class, name = "enum"),
        @JsonSubTypes.Type(value = FloatTypeDefinition.class, name = "float"),
        @JsonSubTypes.Type(value = IntTypeDefinition.class, name = "int"),
        @JsonSubTypes.Type(value = LongTypeDefinition.class, name = "long"),
        @JsonSubTypes.Type(value = MapTypeDefinition.class, name = "map"),
        @JsonSubTypes.Type(value = ObjectTypeDefinition.class, name = "object"),
        @JsonSubTypes.Type(value = ReferenceTypeDefinition.class, name = "ref"),
        @JsonSubTypes.Type(value = ShortTypeDefinition.class, name = "short"),
        @JsonSubTypes.Type(value = StringTypeDefinition.class, name = "string"),
        @JsonSubTypes.Type(value = UnionTypeDefinition.class, name = "union"),
        @JsonSubTypes.Type(value = VoidTypeDefinition.class, name = "void")
})
@JsonInclude(JsonInclude.Include.NON_EMPTY) // do not include any empty or null fields
@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
public abstract class TypeDefinition {

    /**
     * The metadata keyword is legal on any schema, The objects provided must be serializable to JSON.
     * Usually, metadata is for putting things like descriptions or hints for code generators, or other things tools can use.
     */
    private Map<String, ?> metadata;

    /**
     * The list of Decorators that should be applied to this type
     */
    private List<DecoratorDefinition> decorators;

}
