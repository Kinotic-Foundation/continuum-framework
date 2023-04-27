package org.kinotic.continuum.idl.internal.api.converters.jdk;

import org.kinotic.continuum.idl.api.ByteC3Type;
import org.kinotic.continuum.idl.api.C3Type;
import org.kinotic.continuum.idl.internal.api.converters.ConversionContext;
import org.kinotic.continuum.idl.internal.api.converters.SpecificTypeConverter;
import org.springframework.core.ResolvableType;
import org.springframework.stereotype.Component;

/**
 * Created by Navíd Mitchell 🤪 on 4/13/23.
 */
@Component
public class ByteTypeConverter implements SpecificTypeConverter {

    private static final Class<?>[] supports = {byte.class, Byte.class};

    @Override
    public Class<?>[] supports() {
        return supports;
    }

    @Override
    public C3Type convert(ResolvableType resolvableType,
                          ConversionContext conversionContext) {
        return new ByteC3Type();
    }

}

