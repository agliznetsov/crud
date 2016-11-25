package org.crud.showcase.model;

import lombok.Data;
import org.crud.core.data.Identifiable;
import org.hibernate.annotations.*;
import org.hibernate.annotations.CascadeType;

import javax.persistence.*;
import javax.persistence.Entity;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
public class Book implements Identifiable<Long> {
    @Id
    @GeneratedValue
    Long id;

    @Column
    String title;

    @ManyToOne
    Person author;

    @OneToMany(mappedBy = "book")
    @Fetch(FetchMode.SELECT)
    Set<BookChapter> chapters = new HashSet<>();

    @Override
    public String getName() {
        return title;
    }
}
