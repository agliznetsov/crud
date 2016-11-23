package org.crud.hibernate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.crud.core.data.Identifiable;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"id", "product", "amount"})
public class DocumentItem implements Identifiable<Integer> {
    @Id
    @GeneratedValue
    Integer id;

    @ManyToOne
    Document document;

    @ManyToOne
    Product product;

    int amount;
}
