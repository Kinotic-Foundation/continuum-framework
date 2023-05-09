package org.kinotic.continuum.idl.api.converter;

import java.util.List;

/**
 * The {@link IdlConverterStrategy} is used to determine how to convert a Continuum IDL
 * to a specific language type.
 * Created by Navíd Mitchell 🤪 on 4/26/23.
 */
public interface IdlConverterStrategy<T, S> {

    /**
     * @return the {@link SpecificC3TypeConverter}s that this strategy uses
     */
    List<SpecificC3TypeConverter<T, ?, S>> specificTypeConverters();

    /**
     * @return the {@link GenericC3TypeConverter}s that this strategy uses
     */
    List<GenericC3TypeConverter<T, ?, S>> genericTypeConverters();

    /**
     * The object that will be available via the {@link C3ConversionContext#state()}.
     * This can be a simple {@link java.util.Map} or something with better type safety.
     * This should return a new instance each time it is called.
     * This will be called each time a new {@link C3ConversionContext} is created.
     * @return the conversion context state.
     */
    S initialState();

    /**
     * Determines if caching is turned on for this strategy.
     * If shouldCache is true and the {@link C3TypeConverter} extends {@link Cacheable} then the results of the conversion will be cached and reused
     * @return true if the results of the converts should be cached
     */
    boolean shouldCache();

}

