package org.crud.core.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompositeFilter extends Filter {
    CompositeOperator operator = CompositeOperator.AND;
    final List<Filter> filters = new ArrayList<>();

    public CompositeFilter(CompositeOperator operator, Filter... filters) {
        this.operator = operator;
        this.filters.addAll(Arrays.asList(filters));
    }
}
