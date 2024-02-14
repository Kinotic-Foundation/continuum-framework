package org.kinotic.continuum.idl.internal.api.converter;

import org.kinotic.continuum.idl.api.schema.C3Type;
import org.kinotic.continuum.idl.api.converter.C3ConversionContext;
import org.kinotic.continuum.idl.api.converter.SpecificC3TypeConverter;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;

/**
 * Convenience class that allows for multiple specific types to be handled by a single class
 * Created by Navíd Mitchell 🤪 on 4/28/23.
 */
public class MultipleSpecificC3TypeConverter<R, S> implements SpecificC3TypeConverter<R, C3Type, S> {

    private final Map<Class<? extends C3Type>, BiFunction<C3Type, C3ConversionContext<R, S>, R>> converterMap = new HashMap<>();

    @Override
    public Set<Class<? extends C3Type>> supports() {
        return converterMap.keySet();
    }

    @Override
    public R convert(C3Type c3Type, C3ConversionContext<R, S> conversionContext) {
        BiFunction<C3Type, C3ConversionContext<R, S>, R> converter = converterMap.get(c3Type.getClass());
        if(converter != null) {
            return converter.apply(c3Type, conversionContext);
        }else{
            throw new IllegalStateException("Type is supported but no converter was found for " + c3Type.getClass().getName());
        }
    }

    public <T extends C3Type> MultipleSpecificC3TypeConverter<R, S> addConverter(Class<T> clazz, BiFunction<T, C3ConversionContext<R, S>, R> converter){
        //noinspection unchecked
        converterMap.put(clazz, (BiFunction<C3Type, C3ConversionContext<R, S>, R>) converter);
        return this;
    }


}
