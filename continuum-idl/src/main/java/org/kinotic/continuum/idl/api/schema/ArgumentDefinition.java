package org.kinotic.continuum.idl.api.schema;

import lombok.*;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.Validate;
import org.kinotic.continuum.idl.api.schema.decorators.C3Decorator;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides functionality to define an argument for a function with a Continuum schema.
 * The context for equality here is the {@link FunctionDefinition}.
 * Given that no two arguments can have the same name in the same {@link FunctionDefinition}.
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
     * This is the {@link C3Type} that defines the type of this argument.
     */
    @EqualsAndHashCode.Exclude // The context for equality here is the function definition
    private C3Type type;

    /**
     * The list of Decorators that should be applied to this {@link ArgumentDefinition}
     */
    @EqualsAndHashCode.Exclude
    private List<C3Decorator> decorators = new ArrayList<>();

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
    public <T extends C3Decorator> T findDecorator(Class<T> clazz){
        T ret = null;
        if(decorators != null){
            for (C3Decorator decorator : decorators){
                if(clazz.isAssignableFrom(decorator.getClass())){
                    //noinspection unchecked
                    ret = (T) decorator;
                    break;
                }
            }
        }
        return ret;
    }

}
