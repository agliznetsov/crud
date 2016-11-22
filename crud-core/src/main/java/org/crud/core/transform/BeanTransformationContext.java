package org.crud.core.transform;

import lombok.Data;

import java.lang.reflect.Field;

@Data
public class BeanTransformationContext extends TransformationContext {
    private Field sourceField;
    private Field targetField;
}
