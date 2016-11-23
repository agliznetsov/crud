package org.crud.hibernate;

import org.crud.core.data.CrudRepository;
import org.crud.core.data.ResourceQuery;
import org.crud.core.data.ResourceResponse;
import org.crud.core.util.ReflectUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

public abstract class HibernateRepository<T, ID extends Serializable> implements CrudRepository<T, ID> {
    protected Class<T> entityClass;
    protected SaveStrategy saveStrategy = SaveStrategy.UPDATE;
    protected CriteriaBuilder criteriaBuilder;

    public Class<T> getEntityClass() {
        return entityClass;
    }

    public void setEntityClass(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    public void setSaveStrategy(SaveStrategy saveStrategy) {
        this.saveStrategy = saveStrategy;
    }

    public void setCriteriaBuilder(CriteriaBuilder criteriaBuilder) {
        this.criteriaBuilder = criteriaBuilder;
    }

    @Override
    public <S extends T> S save(S entity) {
        if (saveStrategy == SaveStrategy.UPDATE) {
            session().saveOrUpdate(entity);
        } else {
            entity = (S) session().merge(entity);
        }
        return entity;
    }

    @Override
    public T findOne(ID id) {
        return (T) session().get(entityClass, id);
    }

    @Override
    public List<T> findAll() {
        return criteria().list();
    }

    @Override
    public long count() {
        Criteria criteria = criteria();
        criteria.setProjection(Projections.rowCount());
        return ((Number) criteria.uniqueResult()).longValue();
    }

    @Override
    public void delete(ID id) {
        T entity = findOne(id);
        delete(entity);
    }

    @Override
    public void delete(T entity) {
        session().delete(entity);
    }

    @Override
    public void deleteAll() {
        for (T e : findAll()) {
            session().delete(e);
        }
    }

    @Override
    public ResourceResponse<T> query(ResourceQuery query) {
        if (criteriaBuilder == null) {
            throw new IllegalStateException("CriteriaBuilder is not set");
        }
        ResourceResponse<T> response = new ResourceResponse<T>();
        if (query.isCount()) {
            Criteria criteria = criteriaBuilder.countCriteria(criteria(), query);
            Number number = (Number) criteria.uniqueResult();
            response.setCount(number.longValue());
        }
        if (response.getCount() == null || response.getCount() > 0) {
            Criteria criteria = criteriaBuilder.queryCriteria(criteria(), query);
            List list = criteria.list();
            response.setItems(list);
        }
        return response;
    }

    public void useGenericTypes() {
        ParameterizedType pt = ReflectUtils.findParametrizedType(getClass());
        Type[] types = pt.getActualTypeArguments();
        if (types[0] instanceof Class) {
            this.entityClass = (Class<T>) types[0];
        }
    }

    protected abstract Session session();

    protected Criteria criteria() {
        return session().createCriteria(entityClass).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
    }
}
