package org.crud.core.data;

import java.io.Serializable;
import java.util.List;

public interface CrudRepository<T, ID extends Serializable> {
    <S extends T> S save(S entity);

    T findOne(ID id);

    default T getOne(ID id) {
        T entity = findOne(id);
        if (entity == null) {
            throw new IllegalArgumentException("id");
        } else {
            return entity;
        }
    }

    List<T> findAll();

    long count();

    void delete(ID id);

    void delete(T entity);

    void deleteAll();

    ResourceResponse<T> query(ResourceQuery query);
}
