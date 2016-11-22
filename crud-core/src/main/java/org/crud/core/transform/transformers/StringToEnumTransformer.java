package org.crud.core.transform.transformers;


import org.crud.core.transform.ObjectTransformer;
import org.crud.core.transform.TransformationContext;

public class StringToEnumTransformer implements ObjectTransformer<String, Enum> {
    @Override
    public Enum transform(String source, Class targetClass, TransformationContext context) {
        return Enum.valueOf((Class<? extends Enum>) targetClass, source.toUpperCase());
    }
}
