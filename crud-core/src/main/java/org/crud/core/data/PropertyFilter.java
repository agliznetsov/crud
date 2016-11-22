package org.crud.core.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PropertyFilter extends Filter {
    FilterOperator operator;
    String property;
    Object value;
}
