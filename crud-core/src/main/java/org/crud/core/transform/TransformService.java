package org.crud.core.transform;

import java.util.Collection;
import java.util.List;

public interface TransformService {
    <T> T transform(Object source, Class<T> targetClass);

    <T> T transform(Object source, Class<T> targetClass, TransformationContext context);

    <T> List<T> transformList(Collection source, Class<T> targetClass);

    <T> List<T> transformList(Collection source, Class<T> targetClass, TransformationContext context);

    Object transform(Object source);

    Object transform(Object source, TransformationContext context);

    void registerTransformer(Class sourceClass, Class targetClass, ObjectTransformer transformer);

    void registerBeanPair(Class class1, Class class2);

    void registerBeanPair(Class class1, Class class2, PairTransformer pairTransformer);
}
