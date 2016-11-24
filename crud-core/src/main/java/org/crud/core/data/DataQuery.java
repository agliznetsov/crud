package org.crud.core.data;

import lombok.Data;

@Data
public class DataQuery {
    Integer skip;
    Integer max;
    Sort sort;
    boolean count;
    Filter filter;
}
