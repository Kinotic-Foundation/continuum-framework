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

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.*;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.Validate;
import org.kinotic.continuum.idl.api.schema.decorators.C3Decorator;

import java.util.LinkedList;
import java.util.List;

/**
 * Provides functionality to define a function with a Continuum schema.
 * The context for equality here is the {@link ServiceDefinition}.
 * Given that no two functions can have the same name in the same {@link ServiceDefinition}.
 * Created by navid on 2023-4-13
 */
@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString
public class FunctionDefinition extends AbstractDefinition {

    /**
     * The name of this {@link FunctionDefinition}
     */
    private String name;

    /**
     * This is the {@link C3Type} that defines the return type of this function.
     */
    @EqualsAndHashCode.Exclude
    private C3Type returnType = new VoidC3Type();

    /**
     * The list of arguments that this function takes
     */
    @JsonDeserialize(as = LinkedList.class)
    private LinkedList<ParameterDefinition> parameters = new LinkedList<>();

    /**
     * Adds a new parameter to this function
     * @param name the name of the parameter
     * @param c3Type the type of the parameter
     * @return this {@link FunctionDefinition} for chaining
     */
    public FunctionDefinition addParameter(String name, C3Type c3Type){
        ParameterDefinition parameter = new ParameterDefinition(name, c3Type);
        return addParameter(parameter);
    }

    /**
     * Adds a new argument to this function
     * @param name the name of the parameter
     * @param c3Type the type of the parameter
     * @param decorators the decorators to apply to the parameter
     * @return this {@link FunctionDefinition} for chaining
     */
    public FunctionDefinition addParameter(String name, C3Type c3Type, List<C3Decorator> decorators){
        ParameterDefinition parameter = new ParameterDefinition(name, c3Type);
        parameter.setDecorators(decorators);
        return addParameter(parameter);
    }

    /**
     * Adds a new parameter to this function
     * @param parameter the parameter to add
     * @return this {@link FunctionDefinition} for chaining
     */
    public FunctionDefinition addParameter(ParameterDefinition parameter){
        Validate.isTrue(!parameters.contains(parameter), "FunctionDefinition already contains parameter "+parameter.getName());
        parameters.add(parameter);
        return this;
    }

}
