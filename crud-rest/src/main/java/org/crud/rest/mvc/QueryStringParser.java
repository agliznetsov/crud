package org.crud.rest.mvc;

import org.crud.core.data.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Converts query string parameters into DataQuery.
 */
public class QueryStringParser {
    public static final String SKIP = "@skip";
    public static final String MAX = "@max";
    public static final String SORT = "@sort";
    public static final String OP = "@op";
    public static final String COUNT = "@count";
    public static final String DELIMITER = ":";

    private static Map<String, FilterOperator> filterPrefixes = new HashMap<>();

    private DataQuery dataQuery;
    private CompositeFilter compositeFilter;

    static {
        for (FilterOperator e : FilterOperator.values()) {
            filterPrefixes.put(e.name().toLowerCase() + DELIMITER, e);
        }
    }

    private QueryStringParser() {
    }

    public static DataQuery parse(Map<String, List<String>> parameters) {
        return new QueryStringParser().doParse(parameters);
    }

    private DataQuery doParse(Map<String, List<String>> parameters) {
        compositeFilter = new CompositeFilter(CompositeOperator.AND);
        dataQuery = new DataQuery();
        parameters.entrySet().forEach(e -> parseParameter(e.getKey(), e.getValue()));
        if (compositeFilter.getFilters().size() > 0) {
            dataQuery.setFilter(compositeFilter);
        }
        return dataQuery;
    }

    private void parseParameter(String key, List<String> values) {
        switch (key) {
            case SKIP:
                dataQuery.setSkip(Integer.parseInt(first(key, values)));
                break;
            case MAX:
                dataQuery.setMax(Integer.parseInt(first(key, values)));
                break;
            case SORT:
                parseSort(values);
                break;
            case OP:
                compositeFilter.setOperator(CompositeOperator.valueOf(first(key, values).toUpperCase()));
                break;
            case COUNT:
                dataQuery.setCount(true);
                break;
            default:
                parseProperty(key, values);
        }
    }

    private void parseSort(List<String> values) {
        Sort sort = new Sort();
        for (String v : values) {
            String[] parts = v.split(DELIMITER);
            if (parts.length == 1) {
                sort.getOrders().add(new Order(parts[0], OrderDirection.ASC));
            } else {
                sort.getOrders().add(new Order(parts[1], OrderDirection.valueOf(parts[0].toUpperCase())));
            }
        }
        dataQuery.setSort(sort);
    }

    private String first(String key, List<String> values) {
        if (values.size() == 1)
            return values.get(0);
        else
            throw new IllegalArgumentException("Invalid '" + key + "' value count: " + values.size());
    }

    private void parseProperty(String path, List<String> values) {
        if (values.size() > 1) {
            CompositeFilter pf = new CompositeFilter();
            pf.setOperator(CompositeOperator.OR);
            values.forEach(it -> pf.getFilters().add(parseValue(path, it)));
            compositeFilter.getFilters().add(pf);
        } else {
            compositeFilter.getFilters().add(parseValue(path, values.get(0)));
        }
    }

    private Filter parseValue(String key, String value) {
        FilterOperator op = FilterOperator.EQ;
        for (String prefix : filterPrefixes.keySet()) {
            if (value.startsWith(prefix)) {
                value = value.substring(prefix.length());
                op = filterPrefixes.get(prefix);
                break;
            }
        }
        return new PropertyFilter(op, key, value);
    }

}
