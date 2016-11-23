package org.crud.hibernate;

import org.crud.core.data.*;
import org.crud.core.transform.TransformService;
import org.crud.core.util.ReflectUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.*;
import org.hibernate.criterion.Order;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class CriteriaBuilder {

    private final Class domainClass;
    private final TransformService transformService;
    private final Map<String, String> aliases = new HashMap<>();

    public CriteriaBuilder(Class domainClass, TransformService transformService) {
        this.domainClass = domainClass;
        this.transformService = transformService;
    }

    public void createAlias(String path, String alias) {
        aliases.put(alias, path);
    }

    public Criteria queryCriteria(Criteria criteria, ResourceQuery query) {
        if (query.getSort() != null) {
            for (org.crud.core.data.Order order : query.getSort().getOrders()) {
                criteria.addOrder(order.getDirection() == OrderDirection.ASC ? Order.asc(order.getProperty()) : Order.desc(order.getProperty()));
            }
        }

        if (query.getSkip() != null) {
            criteria.setFirstResult(query.getSkip());
        }
        if (query.getMax() != null) {
            criteria.setMaxResults(query.getMax());
        }
        addFilters(query, criteria);

        return criteria;
    }

    public Criteria countCriteria(Criteria criteria, ResourceQuery query) {
        addFilters(query, criteria);
        criteria.setProjection(Projections.rowCount());
        return criteria;
    }

    private void addFilters(ResourceQuery query, Criteria criteria) {
        if (query.getFilter() != null)
            criteria.add(toHibernateCriterion(query.getFilter(), criteria));
    }

    private Criterion toHibernateCriterion(Filter filter, Criteria criteria) {
        if (filter instanceof PropertyFilter) {
            return fromFilter((PropertyFilter) filter, criteria);
        } else {
            CompositeFilter compositeFilter = (CompositeFilter) filter;
            Criterion[] criterions = compositeFilter.getFilters().stream().map(it -> toHibernateCriterion(it, criteria))
                    .collect(Collectors.toList()).toArray(new Criterion[0]);
            if (compositeFilter.getOperator() == CompositeOperator.OR) {
                return Restrictions.or(criterions);
            } else {
                return Restrictions.and(criterions);
            }
        }
    }

    private Criterion fromFilter(PropertyFilter filter, Criteria criteria) {
        String name = filter.getProperty();
        Object value = convertValue(name, filter.getValue());
        switch (filter.getOperator()) {
            case EQ:
                if (value instanceof Collection)
                    return Restrictions.in(name, (Collection) value);
                else
                    return Restrictions.eq(name, value);
            case LT:
                return Restrictions.lt(name, value);
            case LTE:
                return Restrictions.le(name, value);
            case GT:
                return Restrictions.gt(name, value);
            case GTE:
                return Restrictions.ge(name, value);
            case NE:
                return Restrictions.ne(name, value);
            case LIKE:
                return likeCriterion(name, value);
            case ISNULL:
                return Restrictions.isNull(name);
            default:
                throw new IllegalArgumentException("Invalid operation: " + filter.getOperator());
        }
    }

    private Criterion likeCriterion(String name, Object value) {
        return Restrictions.ilike(name, value.toString(), MatchMode.ANYWHERE);
    }

    private Object convertValue(String name, Object value) {
        String path = name;
        for (Map.Entry<String, String> e : aliases.entrySet()) {
            if (name.startsWith(e.getKey())) {
                path = e.getValue() + name.substring(e.getKey().length());
                break;
            }
        }
        Field field = ReflectUtils.getField(domainClass, path);
        Class clazz = field.getType();
        if (value instanceof Collection) {
            return transformService.transformList((Collection) value, clazz);
        } else {
            return transformService.transform(value, clazz);
        }
    }

}
