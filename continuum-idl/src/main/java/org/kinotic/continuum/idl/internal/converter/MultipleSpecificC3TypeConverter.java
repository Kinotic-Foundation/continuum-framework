package org.kinotic.continuum.idl.internal.converter;

import org.kinotic.continuum.idl.api.C3Type;
import org.kinotic.continuum.idl.converter.C3ConversionContext;
import org.kinotic.continuum.idl.converter.SpecificC3TypeConverter;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;

/**
 * Convenience class that allows for multiple specific types to be handled by a single class
 * Created by Navíd Mitchell 🤪 on 4/28/23.
 */
public class MultipleSpecificC3TypeConverter<T> implements SpecificC3TypeConverter<T, C3Type> {

    private final Map<Class<? extends C3Type>, BiFunction<C3Type, C3ConversionContext<T>, T>> converterMap = new HashMap<>();

    @Override
    public Set<Class<? extends C3Type>> supports() {
        return converterMap.keySet();
    }

    @Override
    public T convert(C3Type c3Type, C3ConversionContext<T> conversionContext) {
        BiFunction<C3Type, C3ConversionContext<T>, T> converter = converterMap.get(c3Type.getClass());
        if(converter != null) {
            return converter.apply(c3Type, conversionContext);
        }else{
            throw new IllegalStateException("Type is supported but no converter was found for " + c3Type.getClass().getName());
        }
    }

    public <C3 extends C3Type> MultipleSpecificC3TypeConverter<T> addConverter(Class<C3> clazz, BiFunction<C3, C3ConversionContext<T>, T> converter){
        //noinspection unchecked
        converterMap.put(clazz, (BiFunction<C3Type, C3ConversionContext<T>, T>) converter);
        return this;
    }


}
