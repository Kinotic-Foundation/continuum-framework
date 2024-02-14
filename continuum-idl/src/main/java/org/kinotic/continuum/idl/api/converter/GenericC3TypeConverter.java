package org.kinotic.continuum.idl.api.converter;

import org.kinotic.continuum.idl.api.schema.C3Type;

/**
 * {@link GenericC3TypeConverter} are more general and can convert any {@link C3Type} where supports returns true.
 *
 * @param <R> The type to convert to
 * @param <T> The {@link C3Type} to convert from
 * @param <S> The state type
 *
 * Created by Navíd Mitchell 🤪 on 4/26/23.
 */
public interface GenericC3TypeConverter<R, T extends C3Type, S> extends C3TypeConverter<R, T, S> {

    /**
     * Checks if the given {@link C3Type} is supported by this converter
     *
     * @param c3Type to check if supported
     * @return true if this converter can convert the class false if not
     */
    boolean supports(C3Type c3Type);

}

