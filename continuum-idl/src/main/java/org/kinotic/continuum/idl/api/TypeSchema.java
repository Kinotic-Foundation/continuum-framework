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

import java.util.Map;

/**
 * This is the base class for all type schemas.
 * It can be used to create {@link TypeSchema} from use within a Continuum IDL.
 * <p>
 * Created by navid on 2023-4-13.
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = ArrayTypeSchema.class, name = "array"),
        @JsonSubTypes.Type(value = BooleanTypeSchema.class, name = "boolean"),
        @JsonSubTypes.Type(value = ByteTypeSchema.class, name = "byte"),
        @JsonSubTypes.Type(value = CharTypeSchema.class, name = "char"),
        @JsonSubTypes.Type(value = DateTypeSchema.class, name = "date"),
        @JsonSubTypes.Type(value = DoubleTypeSchema.class, name = "double"),
        @JsonSubTypes.Type(value = EnumTypeSchema.class, name = "enum"),
        @JsonSubTypes.Type(value = FloatTypeSchema.class, name = "float"),
        @JsonSubTypes.Type(value = IntTypeSchema.class, name = "int"),
        @JsonSubTypes.Type(value = LongTypeSchema.class, name = "long"),
        @JsonSubTypes.Type(value = MapTypeSchema.class, name = "map"),
        @JsonSubTypes.Type(value = ObjectTypeSchema.class, name = "object"),
        @JsonSubTypes.Type(value = ReferenceTypeSchema.class, name = "ref"),
        @JsonSubTypes.Type(value = ShortTypeSchema.class, name = "short"),
        @JsonSubTypes.Type(value = StringTypeSchema.class, name = "string"),
        @JsonSubTypes.Type(value = UnionTypeSchema.class, name = "union"),
        @JsonSubTypes.Type(value = VoidTypeSchema.class, name = "void")
})
@JsonInclude(JsonInclude.Include.NON_EMPTY) // do not include any empty or null fields
@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
public abstract class TypeSchema {

    /**
     * You can put nullable on any schema and that will make null be an acceptable value for the schema.
     */
    private Boolean nullable = null;

    /**
     * The metadata keyword is legal on any schema, The objects provided must be serializable to JSON with jackson.
     * Usually, metadata is for putting things like descriptions or hints for code generators, or other things tools can use.
     */
    private Map<String, ?> metadata;

}
