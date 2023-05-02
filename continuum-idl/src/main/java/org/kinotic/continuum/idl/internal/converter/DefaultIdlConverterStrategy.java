package org.kinotic.continuum.idl.internal.converter;

import org.kinotic.continuum.idl.converter.GenericC3TypeConverter;
import org.kinotic.continuum.idl.converter.IdlConverterStrategy;
import org.kinotic.continuum.idl.converter.SpecificC3TypeConverter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Navíd Mitchell 🤪 on 4/28/23.
 */
public class DefaultIdlConverterStrategy<T> implements IdlConverterStrategy<T> {

    private final List<SpecificC3TypeConverter<T, ?>> specificTypeConverters = new ArrayList<>();
    private final List<GenericC3TypeConverter<T, ?>> genericTypeConverters = new ArrayList<>();
    private final boolean shouldCache;

    public DefaultIdlConverterStrategy(boolean shouldCache) {
        this.shouldCache = shouldCache;
    }

    public DefaultIdlConverterStrategy<T> addConverter(SpecificC3TypeConverter<T, ?> converter) {
        specificTypeConverters.add(converter);
        return this;
    }

    public DefaultIdlConverterStrategy<T> addConverter(GenericC3TypeConverter<T, ?> converter) {
        genericTypeConverters.add(converter);
        return this;
    }

    @Override
    public List<SpecificC3TypeConverter<T, ?>> specificTypeConverters() {
        return specificTypeConverters;
    }

    @Override
    public List<GenericC3TypeConverter<T, ?>> genericTypeConverters() {
        return genericTypeConverters;
    }

    @Override
    public boolean shouldCache() {
        return shouldCache;
    }
}
