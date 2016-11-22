package org.crud.core.data;

import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
public class Sort {
    final List<Order> orders;

    public Sort() {
        orders = new ArrayList<>();
    }

    public Sort(Order... orders) {
        this.orders = Arrays.asList(orders);
    }
}
