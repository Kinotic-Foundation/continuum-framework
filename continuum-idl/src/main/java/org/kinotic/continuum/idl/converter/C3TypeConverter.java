package org.kinotic.continuum.idl.converter;

import org.kinotic.continuum.idl.api.C3Type;

/**
 * {@link C3TypeConverter} are the base interface for converting {@link C3Type} to a specific type.
 * All {@link C3TypeConverter}'s should be stateless and thread safe.
 * Created by Navíd Mitchell 🤪 on 4/26/23.
 */
public interface C3TypeConverter<T> {

    /**
     * Converts the given {@link C3Type} to the specific type.
     * @param c3Type to convert
     * @param conversionContext the context to use for conversion
     * @return the converted type
     */
    T convert(C3Type c3Type,
              C3ConversionContext<T> conversionContext);

}
