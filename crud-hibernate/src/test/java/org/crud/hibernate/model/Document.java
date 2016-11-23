package org.crud.hibernate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
public class Document {
    @Id
    @GeneratedValue
    Integer id;

    String name;

    LocalDateTime created;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "document")
    Set<DocumentItem> items = new HashSet<>();
}
