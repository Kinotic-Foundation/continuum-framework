package org.kinotic.continuum.idl.api.schema;

import lombok.*;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.Validate;
import org.kinotic.continuum.idl.api.schema.decorators.C3Decorator;

import java.util.ArrayList;
import java.util.List;

/**
 * Defines a property for a {@link ObjectC3Type}
 * The context for equality here is the {@link ObjectC3Type}.
 * Given no two properties can have the same name in an {@link ObjectC3Type}.
 * Created by Navíd Mitchell 🤪 on 2/22/24.
 */
@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString(callSuper = true)
public class PropertyDefinition {

    /**
     * This is the name of the {@link PropertyDefinition} such as "firstName", "lastName"
     */
    private String name = null;

    /**
     * This is the {@link C3Type} of this {@link PropertyDefinition}
     */
    @EqualsAndHashCode.Exclude
    private C3Type type;

    /**
     * The list of Decorators that should be applied to this {@link PropertyDefinition}
     */
    @EqualsAndHashCode.Exclude
    private List<C3Decorator> decorators = new ArrayList<>();

    /**
     * Adds a new decorator to this {@link PropertyDefinition}
     * @param decorator to add
     * @return this {@link FunctionDefinition} for chaining
     */
    public PropertyDefinition addDecorator(C3Decorator decorator){
        Validate.notNull(decorator, "decorator cannot be null");
        Validate.isTrue(!decorators.contains(decorator), "PropertyDefinition already contains decorator "+decorator);
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
