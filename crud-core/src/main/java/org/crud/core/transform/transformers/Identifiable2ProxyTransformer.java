package org.crud.core.transform.transformers;

import org.crud.core.data.EntityProxy;
import org.crud.core.data.Identifiable;
import org.crud.core.transform.ObjectTransformer;
import org.crud.core.transform.TransformationContext;

public class Identifiable2ProxyTransformer implements ObjectTransformer<Identifiable, EntityProxy> {
    @Override
    public EntityProxy transform(Identifiable source, Class<EntityProxy> targetClass, TransformationContext context) {
        return new EntityProxy(source.getId(), source.getName());
    }
}
