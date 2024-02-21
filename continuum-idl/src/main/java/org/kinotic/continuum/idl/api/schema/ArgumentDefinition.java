package org.kinotic.continuum.idl.api.schema;

import lombok.*;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.Validate;
import org.kinotic.continuum.idl.api.schema.decorators.C3Decorator;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides functionality to define a argument for a function with a Continuum schema.
 * Created by navid on 2023-4-13
 */
@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class ArgumentDefinition {

    /**
     * The name of this {@link ArgumentDefinition}, and the argument name for the {@link FunctionDefinition}
     */
    private String name;

    /**
     * The list of Decorators that should be applied to this {@link ArgumentDefinition}
     */
    @EqualsAndHashCode.Exclude
    private List<C3Decorator> decorators = new ArrayList<>();

    /**
     * This is the {@link C3Type} that defines the type of this argument.
     */
    private C3Type type;

    /**
     * Adds a new decorator to this argument
     * @param decorator to add
     * @return this {@link ArgumentDefinition} for chaining
     */
    public ArgumentDefinition addDecorator(C3Decorator decorator){
        Validate.notNull(decorator, "decorator cannot be null");
        Validate.isTrue(!decorators.contains(decorator), "ArgumentDefinition already contains decorator "+decorator);
        decorators.add(decorator);
        return this;
    }

}
