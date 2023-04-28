package org.kinotic.continuum.idl.converter;

import org.kinotic.continuum.idl.api.C3Type;

/**
 * {@link C3TypeConverter} are the base interface for converting {@link C3Type} to a specific type.
 * Created by Navíd Mitchell 🤪 on 4/26/23.
 */
public interface C3TypeConverter<T> {

    T convert(C3Type c3Type,
              C3ConversionContext<T> conversionContext);

}
