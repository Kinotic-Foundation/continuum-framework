package org.kinotic.continuum.idl.internal.converter;

import org.kinotic.continuum.idl.converter.GenericC3TypeConverter;
import org.kinotic.continuum.idl.converter.IdlConverterStrategy;
import org.kinotic.continuum.idl.converter.SpecificC3TypeConverter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Navíd Mitchell 🤪 on 4/28/23.
 */
public class DefaultIdlConverterStrategy<T, S> implements IdlConverterStrategy<T, S> {

    private final List<SpecificC3TypeConverter<T, ?, S>> specificTypeConverters = new ArrayList<>();
    private final List<GenericC3TypeConverter<T, ?, S>> genericTypeConverters = new ArrayList<>();
    private final S conversionContextState;
    private final boolean shouldCache;

    public DefaultIdlConverterStrategy(S conversionContextState, boolean shouldCache) {
        this.conversionContextState = conversionContextState;
        this.shouldCache = shouldCache;
    }

    public DefaultIdlConverterStrategy<T, S> addConverter(SpecificC3TypeConverter<T, ?, S> converter) {
        specificTypeConverters.add(converter);
        return this;
    }

    public DefaultIdlConverterStrategy<T, S> addConverter(GenericC3TypeConverter<T, ?, S> converter) {
        genericTypeConverters.add(converter);
        return this;
    }

    @Override
    public List<SpecificC3TypeConverter<T, ?, S>> specificTypeConverters() {
        return specificTypeConverters;
    }

    @Override
    public List<GenericC3TypeConverter<T, ?, S>> genericTypeConverters() {
        return genericTypeConverters;
    }

    @Override
    public S conversionContextState() {
        return conversionContextState;
    }

    @Override
    public boolean shouldCache() {
        return shouldCache;
    }
}
