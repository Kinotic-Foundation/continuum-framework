package org.kinotic.continuum.idl.api.converter;

import org.kinotic.continuum.idl.api.schema.C3Type;

/**
 * The {@link IdlConverterStrategy} is used to determine how to convert a Continuum IDL to a specific language type.
 * The {@link IdlConverterStrategy} should be reusable and thread safe.
 *
 * @param <R> The type to convert to
 * @param <S> The state type
 *
 * Created by Navíd Mitchell 🤪 on 4/26/23.
 */
public interface IdlConverterStrategy<R, S> {

    /**
     * Searches for a {@link C3TypeConverter} that can convert the given {@link C3Type}
     * @param c3Type to find a converter for
     * @return the {@link C3TypeConverter} that can convert the given {@link C3Type}
     */
    C3TypeConverter<R, ?, S> converterFor(C3Type c3Type);

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

