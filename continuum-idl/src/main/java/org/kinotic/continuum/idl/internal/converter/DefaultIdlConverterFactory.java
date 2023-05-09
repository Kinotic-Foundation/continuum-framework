package org.kinotic.continuum.idl.internal.converter;

import org.kinotic.continuum.idl.converter.IdlConverter;
import org.kinotic.continuum.idl.converter.IdlConverterFactory;
import org.kinotic.continuum.idl.converter.IdlConverterStrategy;
import org.springframework.stereotype.Component;

/**
 * Created by Navíd Mitchell 🤪 on 4/26/23.
 */
@Component
public class DefaultIdlConverterFactory implements IdlConverterFactory {

    @Override
    public <T, S> IdlConverter<T, S> createConverter(IdlConverterStrategy<T, S> strategy) {
        return new DefaultIdlConverter<>(strategy);
    }

}
