package org.crud.rest.mvc;

import org.crud.core.data.*;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class QueryStringParserTest {

    @Test
    public void empty() throws Exception {
        DataQuery q = QueryStringParser.parse(Collections.emptyMap());
        assertFalse(q.isCount());
        assertNull(q.getFilter());
        assertNull(q.getSkip());
        assertNull(q.getMax());
        assertNull(q.getSort());
    }

    @Test
    public void simple() throws Exception {
        DataQuery q = QueryStringParser.parse(map("@skip=10&@max=5&@count=true@&user.id=1"));
        assertTrue(q.isCount());
        CompositeFilter filter = (CompositeFilter) q.getFilter();
        assertEquals(CompositeOperator.AND, filter.getOperator());
        assertEquals(10, q.getSkip().intValue());
        assertEquals(5, q.getMax().intValue());
        assertNull(q.getSort());
    }

    @Test
    public void single_value() throws Exception {
        DataQuery q = QueryStringParser.parse(map("user.id=1"));
        CompositeFilter filter = (CompositeFilter) q.getFilter();
        assertEquals(1, filter.getFilters().size());
        PropertyFilter pf = (PropertyFilter) filter.getFilters().get(0);
        checkPropertyFilter(pf, "user.id", FilterOperator.EQ, "1");
    }

    @Test
    public void multiple_values() throws Exception {
        DataQuery q = QueryStringParser.parse(map("id=lt:10&id=gt:20"));
        CompositeFilter filter = (CompositeFilter) q.getFilter();
        assertEquals(1, filter.getFilters().size());
        CompositeFilter pf = (CompositeFilter) filter.getFilters().get(0);
        assertEquals(CompositeOperator.OR, pf.getOperator());
        assertEquals(2, pf.getFilters().size());
        checkPropertyFilter((PropertyFilter) pf.getFilters().get(0), "id", FilterOperator.LT, "10");
        checkPropertyFilter((PropertyFilter) pf.getFilters().get(1), "id", FilterOperator.GT, "20");
    }

    @Test
    public void multiple_keys() throws Exception {
        DataQuery q = QueryStringParser.parse(map("user.id=1&name=test&@op=or"));
        CompositeFilter filter = (CompositeFilter) q.getFilter();
        assertEquals(CompositeOperator.OR, filter.getOperator());
        assertEquals(2, filter.getFilters().size());
        checkPropertyFilter((PropertyFilter) filter.getFilters().get(0), "user.id", FilterOperator.EQ, "1");
        checkPropertyFilter((PropertyFilter) filter.getFilters().get(1), "name", FilterOperator.EQ, "test");
    }

    @Test
    public void sort() throws Exception {
        DataQuery q = QueryStringParser.parse(map("@sort=id&@sort=desc:name"));
        assertEquals(2, q.getSort().getOrders().size());
        checkOrder(q.getSort().getOrders().get(0), OrderDirection.ASC, "id");
        checkOrder(q.getSort().getOrders().get(1), OrderDirection.DESC, "name");
    }

    private Map<String, List<String>> map(String query) {
        Map<String, List<String>> map = new HashMap<>();
        String parts[] = query.split("&");
        for (String part : parts) {
            String[] param = part.split("=");
            List<String> values = map.get(param[0]);
            if (values == null) {
                values = new ArrayList<>();
                map.put(param[0], values);
            }
            values.add(param[1]);
        }
        return map;
    }

    private void checkPropertyFilter(PropertyFilter pf, String key, FilterOperator op, String value) {
        assertEquals(key, pf.getProperty());
        assertEquals(op, pf.getOperator());
        assertEquals(value, pf.getValue());
    }

    private void checkOrder(Order order, OrderDirection direction, String name) {
        assertEquals(direction, order.getDirection());
        assertEquals(name, order.getProperty());
    }
}
