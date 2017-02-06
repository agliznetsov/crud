package org.crud.core.util

import org.crud.core.util.model.Foo
import org.crud.core.util.model.Foo2
import org.crud.core.util.model.SubFoo
import spock.lang.Specification

import java.lang.reflect.Field
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 *
 */
class ReflectUtilsTest extends Specification {

    def "Resolve primitive class, if necessary"(Class c, Class result) {
        expect:
        ReflectUtils.resolvePrimitiveIfNecessary(c) == result

        where:
        c             || result
        String.class  || String.class
        Integer.class || Integer.class
        //
        boolean.class || Boolean.class
        byte.class    || Byte.class
        char.class    || Character.class
        double.class  || Double.class
        float.class   || Float.class
        int.class     || Integer.class
        long.class    || Long.class
        short.class   || Short.class
    }

    def "Find the parametrized type of a class"(Class c, ParameterizedType result) {
        expect:
        ReflectUtils.findParametrizedType(c) == result

        where:
        c            || result
        null         || null
        String.class || null
        Foo.class    || Foo.getGenericSuperclass()
        SubFoo.class || Foo.getGenericSuperclass()
    }

    def "Find the generic types of a field"(Field f, Type[] result) {
        expect:
        ReflectUtils.findGenericTypes(f) == result

        where:
        f                           || result
        Foo2.getDeclaredField("f1") || null
        Foo2.getDeclaredField("f2") || [Integer]
        Foo2.getDeclaredField("f3") || [String, Boolean]
    }

    def "Get instance fields of a class"(Class c, Map<String, Field> result) {
        expect:
        ReflectUtils.getInstanceFields(c) == result

        where:
        c          || result
        Foo2.class || [f1: Foo2.getDeclaredField("f1"), f2: Foo2.getDeclaredField("f2"), f3: Foo2.getDeclaredField("f3")]
    }

    def "Get a field given a path"(Class c, String path, Field result) {
        expect:
        ReflectUtils.getField(c, path) == result

        where:
        c         | path       || result
        Foo.class | "f2"       || Foo.getDeclaredField("f2")
        Foo.class | "f2.value" || Optional.getDeclaredField("value")
    }

    def "Get a field given a wrong path"() {
        when:
        ReflectUtils.getField(Foo.class, "f2.xxx")

        then:
        def e = thrown IllegalArgumentException
        e.message == "Field 'xxx' not found in class Optional"
    }
}
