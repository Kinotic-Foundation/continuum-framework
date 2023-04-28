package org.kinotic.continuum.idl.internal.converter;

import org.apache.commons.lang3.Validate;
import org.kinotic.continuum.idl.api.C3Type;
import org.kinotic.continuum.idl.converter.*;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Navíd Mitchell 🤪 on 4/26/23.
 */
public class DefaultConversionContext<T> implements C3ConversionContext<T>{

    private final IdlConverterStrategy<T> strategy;

    private final Map<String, SpecificC3TypeConverter<T>> specificConverters = new LinkedHashMap<>();

    private final Map<C3Type, T> cache = new HashMap<>();

    public DefaultConversionContext(IdlConverterStrategy<T> strategy) {
        this.strategy = strategy;
        for(SpecificC3TypeConverter<T> converter : strategy.specificTypeConverters()){
            for(Class<? extends C3Type> type: converter.supports()){
                Validate.notNull(type, "SpecificC3TypeConverter classes returned from supports must not be null");
                Validate.isTrue(!specificConverters.containsKey(type.getName()),"SpecificC3TypeConverter already exists for "+type.getName());

                specificConverters.put(type.getName(), converter);
            }
        }
    }

    @Override
    public T convert(C3Type c3Type) {
        Validate.notNull(c3Type, "C3Type must not be null");
        C3TypeConverter<T> converter = selectConverter(c3Type);
        Validate.notNull(converter, "Unsupported Class no C3TypeConverter can be found for " + c3Type.getClass().getName());

        boolean cache = strategy.shouldCache() && converter instanceof Cacheable;
        T result = null;

        if(cache){
            result = this.cache.get(c3Type);
        }
        if(result == null) {
            result = converter.convert(c3Type, this);
            if (cache) {
                this.cache.put(c3Type, result);
            }
        }
        return result;
    }

    private C3TypeConverter<T> selectConverter(C3Type type){
        C3TypeConverter<T> ret = null;
        // check specific type then generic converters
        Class<? extends C3Type> clazz = type.getClass();
        if (clazz != null) {
            ret = specificConverters.get(clazz.getName());
        }

        if (ret == null) {
            for (GenericC3TypeConverter<T> converter : strategy.genericTypeConverters()) {
                if (converter.supports(type)) {
                    ret = converter;
                    break;
                }
            }
        }
        return ret;
    }

}
