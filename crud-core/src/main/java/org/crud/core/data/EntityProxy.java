package org.crud.core.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EntityProxy<T> {
    private T id;
    private String name;

    public EntityProxy(T id) {
        this.id = id;
    }
}
