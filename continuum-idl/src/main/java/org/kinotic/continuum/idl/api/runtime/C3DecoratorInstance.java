package org.kinotic.continuum.idl.api.runtime;

import org.kinotic.continuum.idl.api.schema.decorators.C3Decorator;

/**
 * Provides the implementation of a {@link C3Decorator}
 * <p/>
 * What this {@link C3DecoratorInstance} actually does is specific to the application that provides the {@link C3Decorator}
 * Created by Navíd Mitchell 🤪 on 5/9/23.
 */
public interface C3DecoratorInstance {

    /**
     * @return the {@link C3Decorator} class that this instance implements
     */
    Class<? extends C3Decorator> implementsDecorator();

}
