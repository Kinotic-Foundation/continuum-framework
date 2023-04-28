package org.kinotic.continuum.idl.converter;

import org.kinotic.continuum.idl.api.C3Type;

/**
 * {@link GenericC3TypeConverter} are more general and can convert any {@link C3Type} where supports returns true.
 * Created by Navíd Mitchell 🤪 on 4/26/23.
 */
public interface GenericC3TypeConverter<T> extends C3TypeConverter<T> {

    /**
     * Checks if the given {@link C3Type} is supported by this converter
     *
     * @param c3Type to check if supported
     * @return true if this converter can convert the class false if not
     */
    boolean supports(C3Type c3Type);

}
