package org.crud.core.transform.transformers;


import org.crud.core.transform.ObjectTransformer;
import org.crud.core.transform.TransformationContext;

public class EnumToEnumTransformer implements ObjectTransformer<Enum, Enum> {

    @Override
    public Enum transform(Enum source, Class<Enum> targetClass, TransformationContext context) {
        return Enum.valueOf(targetClass, source.name());
    }

}
