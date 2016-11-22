package org.crud.core.transform.transformers;


import org.crud.core.transform.ObjectTransformer;
import org.crud.core.transform.TransformationContext;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class NumberToNumberTransformer implements ObjectTransformer<Number, Number> {
    private static final Map<Class, Function<Number, Number>> converters = new HashMap<>();

    static {
        converters.put(Byte.class, Number::byteValue);
        converters.put(Short.class, Number::shortValue);
        converters.put(Integer.class, Number::intValue);
        converters.put(Long.class, Number::longValue);
        converters.put(Float.class, Number::floatValue);
        converters.put(Double.class, Number::doubleValue);
    }

    @Override
    public Number transform(Number source, Class targetClass, TransformationContext context) {
        Function<Number, Number> converter = converters.get(targetClass);
        if (converter != null)
            return converter.apply(source);
        else
            throw new IllegalArgumentException("Unsupported class: " + targetClass);
    }

}
