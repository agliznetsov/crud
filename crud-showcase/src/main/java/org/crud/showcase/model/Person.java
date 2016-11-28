package org.crud.showcase.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.crud.core.annotations.Filterable;
import org.crud.core.data.Identifiable;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Person implements Identifiable<Long> {
    @Id
    @GeneratedValue
    private Long id;

    @Column
    @Filterable
    private String firstName;

    @Column
    @Filterable
    private String lastName;

    @Column
    private LocalDate dateOfBirth;

    @Override
    public String getName() {
        return lastName + ", " + firstName;
    }
}