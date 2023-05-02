package org.kinotic.continuum.idl.converter;

import org.kinotic.continuum.idl.api.C3Type;

import java.util.Set;

/**
 * {@link SpecificC3TypeConverter} operate on specific {@link C3Type}(s) and convert it to a specific language type.
 * Created by Navíd Mitchell 🤪 on 4/26/23.
 */
public interface SpecificC3TypeConverter<T, C3 extends C3Type> extends C3TypeConverter<T, C3> {

    /**
     * @return the classes that can be converted by this {@link SpecificC3TypeConverter}
     */
    Set<Class<? extends C3Type>> supports();

}
