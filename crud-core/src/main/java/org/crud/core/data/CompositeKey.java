package org.crud.core.data;

public interface CompositeKey {
    String toStringId();

    void parseId(String value);
}
