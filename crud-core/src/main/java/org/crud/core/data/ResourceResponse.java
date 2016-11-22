package org.crud.core.data;

import lombok.Data;

import java.util.List;

@Data
public class ResourceResponse<T> {
    List<T> items;
    Long count;
}
