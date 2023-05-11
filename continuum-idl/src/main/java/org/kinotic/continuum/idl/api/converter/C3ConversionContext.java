package org.kinotic.continuum.idl.api.converter;

import org.kinotic.continuum.idl.api.schema.C3Type;

/**
 * A context that can be used to convert a {@link C3Type} to a specific type.
 * Also, can store custom state for use during or after the conversion process.
 *
 * @param <R> The type to convert to
 * @param <S> The state type
 *
 * Created by Navíd Mitchell 🤪 on 4/26/23.
 */
public interface C3ConversionContext<R, S> {

    R convert(C3Type c3Type);

    S state();

}

