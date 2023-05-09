package org.kinotic.continuum.idl.api.converter;

/**
 * Creates {@link IdlConverter} instances based on a {@link IdlConverterStrategy}
 * Created by Navíd Mitchell 🤪 on 4/26/23.
 */
public interface IdlConverterFactory {

     /**
      * Creates a new {@link IdlConverter} based on the given {@link IdlConverterStrategy}
      * @param strategy to use for conversion
      * @return a new {@link IdlConverter} instance
      * @param <T> the type that the {@link IdlConverter} will convert to
      */
     <T, S> IdlConverter<T, S> createConverter(IdlConverterStrategy<T, S> strategy);

}
