package org.crud.showcase;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.crud.core.transform.ObjectTransformer;
import org.crud.core.transform.TransformationContext;

public class JacksonTransformer implements ObjectTransformer<byte[], Object> {
    final ObjectMapper objectMapper;

    public JacksonTransformer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    @SneakyThrows
    public Object transform(byte[] source, Class<Object> targetClass, TransformationContext context) {
        return objectMapper.readValue(source, targetClass);
    }
}
