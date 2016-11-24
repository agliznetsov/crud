package org.crud.core.data;

import java.util.List;

public interface CrudRepository<T, ID> {
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

    long countAll();

    List<T> find(DataQuery query);

    long count(DataQuery query);

    void deleteOne(ID id);

    void delete(T entity);

    void deleteAll();


}
