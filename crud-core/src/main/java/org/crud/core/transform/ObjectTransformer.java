package org.crud.core.transform;

public interface ObjectTransformer<S, T> {
    T transform(S source, Class<T> targetClass, TransformationContext context);
}
