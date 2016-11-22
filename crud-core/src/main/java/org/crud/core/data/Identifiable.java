package org.crud.core.data;

public interface Identifiable<T> {
    T getId();

    void setId(T id);

    default String getName() {
        return null;
    }
}
