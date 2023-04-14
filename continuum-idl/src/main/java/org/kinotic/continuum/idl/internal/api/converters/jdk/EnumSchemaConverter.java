package org.kinotic.continuum.idl.internal.api.converters.jdk;

import org.apache.commons.lang3.Validate;
import org.kinotic.continuum.idl.api.EnumSchema;
import org.kinotic.continuum.idl.api.Schema;
import org.kinotic.continuum.idl.internal.api.converters.ConversionContext;
import org.kinotic.continuum.idl.internal.api.converters.SpecificTypeSchemaConverter;
import org.springframework.core.ResolvableType;
import org.springframework.stereotype.Component;

/**
 * Created by Navíd Mitchell 🤪 on 4/13/23.
 */
@Component
public class EnumSchemaConverter implements SpecificTypeSchemaConverter {

    @Override
    public Class<?>[] supports() {
        return new Class<?>[] {Enum.class};
    }

    @Override
    public Schema convert(ResolvableType resolvableType,
                          ConversionContext conversionContext) {

        EnumSchema ret = new EnumSchema();

        @SuppressWarnings("unchecked")
        Class<? extends Enum<?>> enumType = (Class<? extends Enum<?>>) resolvableType.resolve();

        Validate.notNull(enumType, "Enum type cannot be resolved");

        for (Enum<?> enumConstant : enumType.getEnumConstants()) {
            ret.addValue(enumConstant.name());
        }

        return ret;
    }

}

