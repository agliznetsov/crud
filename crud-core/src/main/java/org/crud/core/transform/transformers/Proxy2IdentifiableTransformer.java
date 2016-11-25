package org.crud.core.transform.transformers;

import lombok.SneakyThrows;
import org.crud.core.data.EntityProxy;
import org.crud.core.data.Identifiable;
import org.crud.core.transform.ObjectTransformer;
import org.crud.core.transform.TransformationContext;

public class Proxy2IdentifiableTransformer implements ObjectTransformer<EntityProxy, Identifiable> {
    @Override
    @SneakyThrows
    public Identifiable transform(EntityProxy source, Class<Identifiable> targetClass, TransformationContext context) {
        Identifiable target = targetClass.newInstance();
        target.setId(source.getId());
        return target;
    }
}
