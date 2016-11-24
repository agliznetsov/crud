package org.crud.rest.resource;

public interface EntityListener<T> {
    default T beforeSave(T entity) {
        return entity;
    }

    default void afterSave(T entity) {
    }
}
