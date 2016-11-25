package org.crud.showcase.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.crud.core.data.Identifiable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = {"id", "orderNumber", "title"})
public class BookChapter implements Identifiable<Long> {
    @Id
    @GeneratedValue
    Long id;

    @ManyToOne
    Book book;

    String title;

    Integer orderNumber;

}
