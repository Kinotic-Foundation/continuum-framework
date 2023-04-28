package org.kinotic.continuum.idl.converter;

import org.kinotic.continuum.idl.api.C3Type;

/**
 * {@link IdlConverter} allows for conversion of Continuum IDL types to a specific language type.
 * Created by Navíd Mitchell 🤪 on 4/26/23.
 */
public interface IdlConverter<T> {

    /**
     * Converts the given {@link C3Type} to the specific language type.
     * @param type to convert
     * @return the converted type
     */
    T convert(C3Type type);

    /**
     * The {@link C3ConversionContext} that is used during the conversion process.
     * Ths allows {@link C3TypeConverter} to store information needed after the conversion process.
     * @return the {@link C3ConversionContext} used during conversion
     */
    C3ConversionContext<T> getConversionContext();

}
