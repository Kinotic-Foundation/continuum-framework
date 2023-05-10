package org.kinotic.continuum.idl.api.schema.decorators;

import java.util.List;

/**
 * Represents a {@link C3Decorator} the signifies that the decorated value must not be null
 * Created by Navíd Mitchell 🤪 on 4/23/23.
 */
public class NotNullC3Decorator extends C3Decorator{

    public NotNullC3Decorator() {
        targets = List.of(DecoratorTarget.FIELD, DecoratorTarget.PARAMETER);
    }
}
