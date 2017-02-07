package org.crud.core.util;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.crud.core.util.MapUtils.*;
import static org.junit.Assert.*;

public class MapUtilsTest {
    @Test
    public void empty_map() throws Exception {
        assertEquals(new HashMap<>(), map());
    }

    @Test
    public void create_map() throws Exception {
        Map<String, Number> map = map("a", 1, "a", 2, "a.b", 10);
        assertEquals(2, map.size());
        assertEquals(2, map.get("a"));
        assertEquals(10, map.get("a.b"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalid_parameters_map() throws Exception {
        map("a", 1, "b");
    }

    @Test(expected = IllegalArgumentException.class)
    public void null_path() throws Exception {
        getValue(map());
    }

    @Test
    public void create_deep_map() throws Exception {
        Map map = deepMap("a.b.c", 1);
        assertEquals(1, map.size());
        assertTrue(map.get("a") instanceof Map);
        assertEquals(new Integer(1), getValue(map, "a.b.c"));
    }

    @Test
    public void test_getPath() throws Exception {
        Map map = deepMap("a.b.c", 1);
        assertEquals(new Integer(1), getValue(map, "a.b.c"));
        assertNull(null, getValue(map, "a.b.c.d.e"));
    }
}
