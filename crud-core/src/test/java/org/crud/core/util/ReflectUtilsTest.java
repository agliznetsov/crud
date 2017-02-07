package org.crud.core.util;

import org.crud.core.beans.Foo;
import org.crud.core.beans.Foo2;
import org.crud.core.beans.SubFoo;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Map;

import static org.junit.Assert.*;

public class ReflectUtilsTest {
    @Test
    public void resolvePrimitiveIfNecessary() throws Exception {
        Map<Class, Class> input = MapUtils.map(
                String.class, String.class,
                Integer.class, Integer.class,
                boolean.class, Boolean.class,
                byte.class, Byte.class,
                char.class, Character.class,
                double.class, Double.class,
                float.class, Float.class,
                int.class, Integer.class,
                long.class, Long.class,
                short.class, Short.class
        );
        input.forEach((key, value) -> {
            assertEquals(value, ReflectUtils.resolvePrimitiveIfNecessary(key));
        });
    }

    @Test
    public void findParametrizedType() throws Exception {
        assertNull(null, ReflectUtils.findParametrizedType(null));
        assertNull(null, ReflectUtils.findParametrizedType(String.class));
        assertEquals("java.util.ArrayList<java.lang.Integer>", String.valueOf(ReflectUtils.findParametrizedType(Foo.class)));
        assertEquals("java.util.ArrayList<java.lang.Integer>", String.valueOf(ReflectUtils.findParametrizedType(SubFoo.class)));
    }

    @Test
    public void findGenericTypes() throws Exception {
        assertNull(null, ReflectUtils.findGenericTypes(Foo2.class.getDeclaredField("f1")));
        assertArrayEquals(new Type[]{Integer.class}, ReflectUtils.findGenericTypes(Foo2.class.getDeclaredField("f2")));
        assertArrayEquals(new Type[]{String.class, Boolean.class}, ReflectUtils.findGenericTypes(Foo2.class.getDeclaredField("f3")));
    }

    @Test
    public void getInstanceFields() throws Exception {
        Map<String, Field> fields = ReflectUtils.getInstanceFields(Foo2.class);
        assertEquals(3, fields.size());
        assertEquals("f1", fields.get("f1").getName());
        assertEquals("f2", fields.get("f2").getName());
        assertEquals("f3", fields.get("f3").getName());
    }

    @Test
    public void getField() throws Exception {
        assertEquals("f2", ReflectUtils.getField(Foo.class, "f2").getName());
        assertEquals("value", ReflectUtils.getField(Foo.class, "f2.value").getName());

    }

    @Test(expected = IllegalArgumentException.class)
    public void getField_wrong_path() throws Exception {
        ReflectUtils.getField(Foo.class, "f2.xxx");
    }

}
