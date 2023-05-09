package org.kinotic.continuum.idl.internal.api.converter;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.kinotic.continuum.idl.api.converter.*;
import org.kinotic.continuum.idl.api.schema.C3Type;
import org.kinotic.continuum.idl.converter.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by Navíd Mitchell 🤪 on 4/26/23.
 */
public class DefaultC3ConversionContext<T, S> implements C3ConversionContext<T, S> {

    private static final Logger log = LoggerFactory.getLogger(DefaultC3ConversionContext.class);

    private final IdlConverterStrategy<T, S> strategy;

    private final Map<String, SpecificC3TypeConverter<T, ?, S>> specificConverters = new LinkedHashMap<>();

    private final Deque<C3Type> conversionDepthStack = new ArrayDeque<>();

    private final Deque<C3Type> errorStack = new ArrayDeque<>();

    private final Map<C3Type, T> cache = new HashMap<>();

    private final S state;

    public DefaultC3ConversionContext(IdlConverterStrategy<T, S> strategy) {
        this.strategy = strategy;
        for(SpecificC3TypeConverter<T, ?, S> converter : strategy.specificTypeConverters()){
            for(Class<? extends C3Type> type: converter.supports()){
                Validate.notNull(type, "SpecificC3TypeConverter classes returned from supports must not be null");
                Validate.isTrue(!specificConverters.containsKey(type.getName()),"SpecificC3TypeConverter already exists for "+type.getName());

                specificConverters.put(type.getName(), converter);
            }
        }

        this.state = strategy.initialState();

    }

    @Override
    public T convert(C3Type c3Type) {
        try {
            Validate.notNull(c3Type, "C3Type must not be null");

            //FIXME: add JSR-380 validation

            conversionDepthStack.addFirst(c3Type);

            //noinspection unchecked
            C3TypeConverter<T, C3Type, S> converter = (C3TypeConverter<T, C3Type, S>) selectConverter(c3Type);
            Validate.isTrue(converter != null, "Unsupported Class no C3TypeConverter can be found for " + c3Type.getClass().getName());

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
        } catch (Exception e) {
            logException(e);
            throw e;
        } finally {
            conversionDepthStack.removeFirst();
        }
    }

    private C3TypeConverter<T, ?, S> selectConverter(C3Type type){
        C3TypeConverter<T, ?, S> ret = null;
        // check specific type then generic converters
        Class<? extends C3Type> clazz = type.getClass();
        if (clazz != null) {
            ret = specificConverters.get(clazz.getName());
        }

        if (ret == null) {
            for (GenericC3TypeConverter<T, ?, S> converter : strategy.genericTypeConverters()) {
                if (converter.supports(type)) {
                    ret = converter;
                    break;
                }
            }
        }
        return ret;
    }

    @Override
    public S state() {
        return state;
    }

    /**
     * Log an exception when appropriate dealing with only logging once even when recursion has occurred
     * @param e to log
     */
    private void logException(Exception e){
        if(log.isDebugEnabled() || log.isTraceEnabled()){
            // This indicates this is the first time logException has been called for this context.
            // This would occur at the furthest call depth so at this point the conversionDepthStack has the complete stack
            if(errorStack.isEmpty()){
                // We loop vs add all to keep stack intact
                for(C3Type c3Type: conversionDepthStack){
                    errorStack.addFirst(c3Type);
                }
            }
            if(conversionDepthStack.size() == 1) { // we are at the top of the stack during recursion
                StringBuilder sb = new StringBuilder("Error occurred during conversion.\n" + e.getMessage() + "\n");
                int objectCount = 1;
                for (C3Type c3Type : errorStack) {
                    sb.append(StringUtils.leftPad("", objectCount, '\t'));
                    sb.append("- ");
                    sb.append(c3Type.toString());
                    sb.append("\n");
                    objectCount++;
                }
                log.debug(sb.toString());
                errorStack.clear(); // we have printed reset
            }
        }
    }

}
