package org.crud.core.util;

import lombok.SneakyThrows;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

import static java.lang.reflect.Modifier.isStatic;

public abstract class ReflectUtils {
    private static final Map<Class, Map<String, Field>> fieldsCache = new HashMap<>();
    private static final Map<Class<?>, Class<?>> primitiveWrappers = new IdentityHashMap<Class<?>, Class<?>>(8);

    static {
        primitiveWrappers.put(boolean.class, Boolean.class);
        primitiveWrappers.put(byte.class, Byte.class);
        primitiveWrappers.put(char.class, Character.class);
        primitiveWrappers.put(double.class, Double.class);
        primitiveWrappers.put(float.class, Float.class);
        primitiveWrappers.put(int.class, Integer.class);
        primitiveWrappers.put(long.class, Long.class);
        primitiveWrappers.put(short.class, Short.class);
    }

    public static Class<?> resolvePrimitiveIfNecessary(Class<?> clazz) {
        return (clazz.isPrimitive() && clazz != void.class ? primitiveWrappers.get(clazz) : clazz);
    }

    public static ParameterizedType findParametrizedType(Class clazz) {
        while (clazz != null) {
            if (clazz.getGenericSuperclass() instanceof ParameterizedType) {
                return ((ParameterizedType) clazz.getGenericSuperclass());
            }
            clazz = clazz.getSuperclass();
        }
        return null;
    }

    public static Type[] findGenericTypes(Field field) {
        if (field.getGenericType() != null && field.getGenericType() instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) field.getGenericType();
            return pt.getActualTypeArguments();
        }
        return null;
    }

    public static <A extends Annotation> A getInheritedAnnotation(Class<A> annotationClass, AnnotatedElement element) {
        A annotation = element.getAnnotation(annotationClass);
        if (annotation == null && element instanceof Method)
            annotation = getOverriddenAnnotation(annotationClass, (Method) element);
        return annotation;
    }

    private static <A extends Annotation> A getOverriddenAnnotation(Class<A> annotationClass, Method method) {
        final Class<?> methodClass = method.getDeclaringClass();
        final String name = method.getName();
        final Class<?>[] params = method.getParameterTypes();

        // prioritize all superclasses over all interfaces
        final Class<?> superclass = methodClass.getSuperclass();
        if (superclass != null) {
            final A annotation =
                    getOverriddenAnnotationFrom(annotationClass, superclass, name, params);
            if (annotation != null)
                return annotation;
        }

        // depth-first over interface hierarchy
        for (final Class<?> intf : methodClass.getInterfaces()) {
            final A annotation =
                    getOverriddenAnnotationFrom(annotationClass, intf, name, params);
            if (annotation != null)
                return annotation;
        }

        return null;
    }

    private static <A extends Annotation> A getOverriddenAnnotationFrom(
            Class<A> annotationClass, Class<?> searchClass, String name, Class<?>[] params) {
        try {
            final Method method = searchClass.getMethod(name, params);
            final A annotation = method.getAnnotation(annotationClass);
            if (annotation != null)
                return annotation;
            return getOverriddenAnnotation(annotationClass, method);
        } catch (final NoSuchMethodException e) {
            return null;
        }
    }

    public static ParameterizedType getInheritedGenericReturnType(Method method) {
        return getInheritedGenericReturnType(method.getDeclaringClass(), method.getName(), method.getParameterTypes());
    }

    private static ParameterizedType getInheritedGenericReturnType(Class<?> clazz, String name, Class<?>[] params) {
        try {
            Method method = clazz.getMethod(name, params);
            if (method.getGenericReturnType() instanceof ParameterizedType)
                return (ParameterizedType) method.getGenericReturnType();
        } catch (final NoSuchMethodException e) {
            return null;
        }

        if (clazz.getSuperclass() != null)
            return getInheritedGenericReturnType(clazz.getSuperclass(), name, params);
        else
            return null;
    }

    //TODO: get rid of synchronized
    public static synchronized Map<String, Field> getInstanceFields(Class o) {
        Map<String, Field> map = fieldsCache.get(o);
        if (map == null) {
            map = new HashMap<>();
            Class clazz = o;
            while (clazz != null) {
                for (Field f : clazz.getDeclaredFields()) {
                    if (!isStatic(f.getModifiers())) {
                        f.setAccessible(true);
                        map.put(f.getName(), f);
                    }
                }
                clazz = clazz.getSuperclass();
            }
            fieldsCache.put(o, map);
        }
        return map;
    }

    public static Field getField(Class clazz, String path) {
        Field field = null;
        String[] parts = path.split("\\.");
        for (String part : parts) {
            Map<String, Field> fields = getInstanceFields(clazz);
            field = fields.get(part);
            if (field == null)
                throw new IllegalArgumentException("Field '" + part + "' not found in class " + clazz.getSimpleName());
            else
                clazz = field.getType();
        }
        return field;
    }

    @SneakyThrows
    public static void setField(Object object, String path, Object value) {
        Field field = getField(object.getClass(), path);
        field.set(object, value);
    }
}
