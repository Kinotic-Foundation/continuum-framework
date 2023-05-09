package org.kinotic.continuum.idl.converter;

import org.kinotic.continuum.idl.api.C3Type;

import java.util.Map;

/**
 * A context that can be used to convert a {@link C3Type} to a specific type.
 * Also, can store custom state for use during or after the conversion process.
 * Created by Navíd Mitchell 🤪 on 4/26/23.
 */
public interface C3ConversionContext<T> {

    T convert(C3Type c3Type);

    Map<String, Object> state();
}
