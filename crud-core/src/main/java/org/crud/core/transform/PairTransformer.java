package org.crud.core.transform;

public interface PairTransformer<T1, T2> {
    T2 forward(T1 source, T2 target, TransformationContext context);

    T1 backward(T2 source, T1 target, TransformationContext context);
}
