package org.crud.core.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    String property;
    OrderDirection direction = OrderDirection.ASC;

    public Order(String name) {
        property = name;
    }
}
