package org.crud.core.transform.transformers;

import lombok.SneakyThrows;
import org.crud.core.transform.BeanTransformationContext;
import org.crud.core.transform.ObjectTransformer;
import org.crud.core.transform.TransformService;
import org.crud.core.transform.TransformationContext;
import org.crud.core.util.ReflectUtils;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class BeanTransformer implements ObjectTransformer {
    private final TransformService transformService;
    private final Class class1, class2;
    private final Map<String, Field> fields1;
    private final Map<String, Field> fields2;
    private boolean ignoreIncompatibleFields = false;

    public BeanTransformer(TransformService transformService, Class class1, Class class2) {
        this.transformService = transformService;
        this.class1 = class1;
        this.class2 = class2;
        fields1 = ReflectUtils.getInstanceFields(class1);
        fields2 = ReflectUtils.getInstanceFields(class2);
    }

    public void setIgnoreIncompatibleFields(boolean ignoreIncompatibleFields) {
        this.ignoreIncompatibleFields = ignoreIncompatibleFields;
    }

    @Override
    @SneakyThrows
    public Object transform(Object source, Class targetClass, TransformationContext context) {
        if (source == null)
            return null;

        if (source.getClass().equals(class1))
            return transform(source, class2.newInstance(), fields1, fields2, context);
        else if (source.getClass().equals(class2))
            return transform(source, class1.newInstance(), fields2, fields1, context);
        else
            throw new IllegalArgumentException("Invalid source class: " + source.getClass() + " excepted " + class1 + " or " + class2);
    }

    @SneakyThrows
    private Object transform(Object source, Object target, Map<String, Field> sourceFields, Map<String, Field> targetFields, TransformationContext context) {
        for (Map.Entry<String, Field> e : sourceFields.entrySet()) {
            Field targetField = targetFields.get(e.getKey());
            if (targetField != null) {
                copyField(source, target, e.getValue(), targetField, context);
            }
        }
        return target;
    }

    private void copyField(Object source, Object target, Field sourceField, Field targetField, TransformationContext parentContext) throws IllegalAccessException {
        if (isCollection(sourceField) && isCollection(targetField)) {
            Collection sourceCollection = (Collection) sourceField.get(source);
            Collection targetCollection = (Collection) targetField.get(target);
            if (targetCollection != null) {
                targetCollection.clear();
            }
            copyCollection(sourceCollection, targetCollection, sourceField, targetField, parentContext);
        } else if (isMap(sourceField) && isMap(targetField)) {
            Map sourceMap = (Map) sourceField.get(source);
            if (sourceMap != null) {
                Map targetMap = (Map) targetField.get(target);
                if (targetMap == null) {
                    targetMap = new HashMap<>();
                    targetField.set(target, targetMap);
                }
                copyMap(sourceMap, targetMap, sourceField, targetField, parentContext);
            }
        } else {
            Object value = sourceField.get(source);
            if (value != null && !sourceField.getType().equals(targetField.getType())) {
                BeanTransformationContext context = new BeanTransformationContext();
                context.setSourceField(sourceField);
                context.setTargetField(targetField);
                try {
                    value = transformService.transform(value, targetField.getType(), context);
                } catch (IllegalArgumentException e) {
                    if (ignoreIncompatibleFields)
                        return;
                    else
                        throw e;
                }
            }
            targetField.set(target, value);
        }
    }

    @SneakyThrows
    private void copyCollection(Collection source, Collection target, Field sourceField, Field targetField, TransformationContext parentContext) {
        if (source != null && target != null) {
            Type[] sourceClasses = ReflectUtils.findGenericTypes(sourceField);
            Type[] targetClasses = ReflectUtils.findGenericTypes(targetField);
            if (sourceClasses != null && sourceClasses.length == 1 &&
                    targetClasses != null && targetClasses.length == 1 &&
                    !sourceClasses[0].equals(targetClasses[0])) {
                BeanTransformationContext context = new BeanTransformationContext();
                context.setTargetField(targetField);
                context.setSourceField(sourceField);
                try {
                    for (Object value : source) {
                        target.add(transformService.transform(value, findClass(targetClasses[0]), context));
                    }
                } catch (IllegalArgumentException e) {
                    if (ignoreIncompatibleFields)
                        return;
                    else
                        throw e;
                }
            } else {
                for (Object o : source)
                    target.add(o);
            }
        }
    }

    private void copyMap(Map source, Map target, Field sourceField, Field targetField, TransformationContext parentContext) {
        if (source != null && target != null) {
            Type[] sourceClasses = ReflectUtils.findGenericTypes(sourceField);
            Type[] targetClasses = ReflectUtils.findGenericTypes(targetField);
            boolean transformKey = sourceClasses != null && targetClasses != null && sourceClasses.length == 2 && targetClasses.length == 2 && !sourceClasses[0].equals(targetClasses[0]);
            boolean transformValue = sourceClasses != null && targetClasses != null && sourceClasses.length == 2 && targetClasses.length == 2 && !sourceClasses[1].equals(targetClasses[1]);
            if (transformKey || transformValue) {
                BeanTransformationContext context = new BeanTransformationContext();
                context.setTargetField(targetField);
                context.setSourceField(sourceField);
                for (Object key : source.keySet()) {
                    Object targetKey = transformKey ? transformService.transform(key, findClass(targetClasses[0]), context) : key;
                    Object targetValue = transformValue ? transformService.transform(source.get(key), findClass(targetClasses[1]), context) : source.get(key);
                    target.put(targetKey, targetValue);
                }
            } else {
                for (Map.Entry e : (Set<Map.Entry>) source.entrySet())
                    target.put(e.getKey(), e.getValue());
            }
        }
    }

    private Class findClass(Type t) {
        if (t instanceof ParameterizedType)
            return (Class) ((ParameterizedType) t).getRawType();
        else
            return (Class) t;
    }

    private boolean isCollection(Field field) {
        return Collection.class.isAssignableFrom(field.getType());
    }

    private boolean isMap(Field field) {
        return Map.class.isAssignableFrom(field.getType());
    }

}
