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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Provides functionality to define a function with a Continuum schema.
 * Created by navid on 2023-4-13
 */
@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class FunctionDefinition {

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
     * The list of Decorators that should be applied to this {@link FunctionDefinition}
     */
    @EqualsAndHashCode.Exclude
    private List<C3Decorator> decorators = new ArrayList<>();

    /**
     * The list of arguments that this function takes
     */
    @EqualsAndHashCode.Exclude
    @JsonDeserialize(as = LinkedList.class)
    private List<ArgumentDefinition> arguments = new LinkedList<>();

    /**
     * Adds a new argument to this function
     * @param name the name of the argument
     * @param c3Type the type of the argument
     * @return this {@link FunctionDefinition} for chaining
     */
    public FunctionDefinition addArgument(String name, C3Type c3Type){
        ArgumentDefinition argument = new ArgumentDefinition().setName(name).setType(c3Type);
        Validate.isTrue(!arguments.contains(argument), "FunctionDefinition already contains argument "+argument.getName());
        arguments.add(argument);
        return this;
    }

    /**
     * Adds a new argument to this function
     * @param name the name of the argument
     * @param c3Type the type of the argument
     * @param decorators the decorators to apply to the argument
     * @return this {@link FunctionDefinition} for chaining
     */
    public FunctionDefinition addArgument(String name, C3Type c3Type, List<C3Decorator> decorators){
        ArgumentDefinition argument = new ArgumentDefinition().setName(name).setType(c3Type).setDecorators(decorators);
        Validate.isTrue(!arguments.contains(argument), "FunctionDefinition already contains argument "+argument.getName());
        arguments.add(argument);
        return this;
    }

    /**
     * Adds a new argument to this function
     * @param argument the argument to add
     * @return this {@link FunctionDefinition} for chaining
     */
    public FunctionDefinition addArgument(ArgumentDefinition argument){
        Validate.isTrue(!arguments.contains(argument), "FunctionDefinition already contains argument "+argument.getName());
        arguments.add(argument);
        return this;
    }

    /**
     * Adds a new decorator to this function
     * @param decorator to add
     * @return this {@link FunctionDefinition} for chaining
     */
    public FunctionDefinition addDecorator(C3Decorator decorator){
        Validate.notNull(decorator, "decorator cannot be null");
        Validate.isTrue(!decorators.contains(decorator), "FunctionDefinition already contains decorator "+decorator);
        decorators.add(decorator);
        return this;
    }

}
