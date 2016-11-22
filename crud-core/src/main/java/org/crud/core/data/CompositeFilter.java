package org.crud.core.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompositeFilter extends Filter {
    CompositeOperator operator = CompositeOperator.AND;
    final List<Filter> filters = new ArrayList<>();
}
