package org.crud.core.transform.transformers;

import lombok.SneakyThrows;
import org.crud.core.transform.ObjectTransformer;
import org.crud.core.transform.TransformationContext;
import org.crud.core.util.DateUtils;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class StringToObjectTransformer implements ObjectTransformer<String, Object> {
    private static final Map<Class, Function<String, Object>> parsers = new HashMap<>();

    static {
        parsers.put(Byte.class, Byte::parseByte);
        parsers.put(Short.class, Short::parseShort);
        parsers.put(Integer.class, Integer::parseInt);
        parsers.put(Long.class, Long::parseLong);
        parsers.put(Float.class, Float::parseFloat);
        parsers.put(Double.class, Double::parseDouble);
        parsers.put(Boolean.class, Boolean::parseBoolean);
        parsers.put(ZonedDateTime.class, DateUtils::parseZonedDateTime);
        parsers.put(LocalDateTime.class, LocalDateTime::parse);
    }

    @Override
    @SneakyThrows
    public Object transform(String source, Class<Object> targetClass, TransformationContext context) {
        Function<String, Object> parser = parsers.get(targetClass);
        if (parser != null)
            return parser.apply(source);
        else
            throw new IllegalArgumentException("Unsupported class: " + targetClass);
    }
}
