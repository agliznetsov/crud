package org.crud.core.transform;


import org.crud.core.data.EntityProxy;
import org.crud.core.data.Identifiable;
import org.crud.core.transform.transformers.*;
import org.crud.core.util.ReflectUtils;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class TransformServiceImpl implements TransformService {
    Map<Class, Map<Class, ObjectTransformer>> transformers = new HashMap<>();
    List<PairTransformer> pairTransformers = new ArrayList<>();
    EnumToEnumTransformer enumTransformer = new EnumToEnumTransformer();
    NumberToNumberTransformer numberTransformer = new NumberToNumberTransformer();

    public TransformServiceImpl() {
        registerStandardTransformers();
    }

    protected void registerStandardTransformers() {
        StringToObjectTransformer toObjectTransformer = new StringToObjectTransformer();
        registerTransformer(String.class, Byte.class, toObjectTransformer);
        registerTransformer(String.class, Short.class, toObjectTransformer);
        registerTransformer(String.class, Integer.class, toObjectTransformer);
        registerTransformer(String.class, Long.class, toObjectTransformer);
        registerTransformer(String.class, Float.class, toObjectTransformer);
        registerTransformer(String.class, Double.class, toObjectTransformer);
        registerTransformer(String.class, Boolean.class, toObjectTransformer);
        registerTransformer(String.class, ZonedDateTime.class, toObjectTransformer);
        registerTransformer(String.class, LocalDateTime.class, toObjectTransformer);
        registerTransformer(String.class, Enum.class, new StringToEnumTransformer());
        registerTransformer(Identifiable.class, EntityProxy.class, new Identifiable2ProxyTransformer());
        registerTransformer(EntityProxy.class, Identifiable.class, new Proxy2IdentifiableTransformer());
    }

    @Override
    public <T> T transform(Object source, Class<T> targetClass) {
        return transform(source, targetClass, new TransformationContext());
    }

    @Override
    public <T> T transform(Object source, Class<T> targetClass, TransformationContext context) {
        if (source == null)
            return null;

        Class wrapper = ReflectUtils.resolvePrimitiveIfNecessary(targetClass);

        if (source.getClass().equals(wrapper) || source.getClass().equals(targetClass))
            return (T) source;

        ObjectTransformer transformer = findTransformer(source.getClass(), wrapper);
        return (T) transformer.transform(source, wrapper, context);
    }

    @Override
    public <T> List<T> transformList(Collection source, Class<T> targetClass) {
        return transformList(source, targetClass, new TransformationContext());
    }

    @Override
    public <T> List<T> transformList(Collection source, Class<T> targetClass, TransformationContext context) {
        if (source == null)
            return null;
        else
            return (List<T>) source.stream().map(it -> transform(it, targetClass, context)).collect(Collectors.toList());
    }

    @Override
    public Object transform(Object source, TransformationContext context) {
        if (source == null)
            return null;

        Map<Class, ObjectTransformer> map = transformers.get(source.getClass());
        if (map == null) {
            return source;
        }

        if (map.size() != 1) {
            throw new IllegalArgumentException("Too many Possible transformation targets of class: " + source.getClass());
        }

        Map.Entry<Class, ObjectTransformer> e = map.entrySet().iterator().next();
        return e.getValue().transform(source, e.getKey(), context);
    }

    @Override
    public Object transform(Object source) {
        return transform(source, new TransformationContext());
    }

    @Override
    public void registerTransformer(Class sourceClass, Class targetClass, ObjectTransformer transformer) {
        Map<Class, ObjectTransformer> map = transformers.get(sourceClass);
        if (map == null) {
            map = new HashMap<>();
            transformers.put(sourceClass, map);
        }
        map.put(targetClass, transformer);
    }

    @Override
    public void registerBeanPair(Class class1, Class class2) {
        BeanTransformer beanTransformer = new BeanTransformer(this, class1, class2);
        registerTransformer(class1, class2, beanTransformer);
        registerTransformer(class2, class1, beanTransformer);
    }

    @Override
    public void registerBeanPair(Class class1, Class class2, PairTransformer pairTransformer) {
        BeanTransformer beanTransformer = new BeanTransformer(this, class1, class2);
        registerTransformer(class1, class2, (source, targetClass, context) -> {
            Object target = beanTransformer.transform(source, targetClass, context);
            return pairTransformer.forward(source, target, context);
        });
        registerTransformer(class2, class1, (source, targetClass, context) -> {
            Object target = beanTransformer.transform(source, targetClass, context);
            return pairTransformer.backward(source, target, context);
        });
        pairTransformers.add(pairTransformer);
    }

    private <T> ObjectTransformer findTransformer(Class sourceClass, Class<T> targetClass) {
        // lookup first exact match for source
        Map<Class, ObjectTransformer> tmp = transformers.get(sourceClass);
        if (tmp != null) {
            ObjectTransformer res = findTransformer(targetClass, tmp);
            if (res != null)
                return res;
        }

        // try lookup with isAssignableFrom for source
        for (Map.Entry<Class, Map<Class, ObjectTransformer>> e : transformers.entrySet()) {
            if (e.getKey().isAssignableFrom(sourceClass)) {
                ObjectTransformer res = findTransformer(targetClass, e.getValue());
                if (res != null)
                    return res;
            }
        }

        if (sourceClass.isEnum() && targetClass.isEnum()) {
            return enumTransformer;
        } else if (Number.class.isAssignableFrom(sourceClass) && Number.class.isAssignableFrom(targetClass)) {
            return numberTransformer;
        } else if (targetClass.equals(String.class)) {
            return (source, targetClass1, context) -> objectToString(source);
        } else {
            throw new IllegalArgumentException("Transformer not found from " + sourceClass + " to " + targetClass);
        }
    }

    private static String objectToString(Object source) {
        return source == null ? null : source.toString();
    }

    private ObjectTransformer findTransformer(Class targetClass, Map<Class, ObjectTransformer> map) {
        ObjectTransformer transformer = map.get(targetClass);
        if (transformer != null)
            return transformer;
        else {
            for (Map.Entry<Class, ObjectTransformer> e : map.entrySet()) {
                if (targetClass.isAssignableFrom(e.getKey()) || e.getKey().isAssignableFrom(targetClass)) {
                    return e.getValue();
                }
            }
        }
        return null;
    }

}
