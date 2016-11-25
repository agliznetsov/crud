package org.crud.showcase.dto;

import lombok.Data;
import org.crud.core.data.EntityProxy;
import org.crud.core.data.Identifiable;
import org.crud.showcase.model.Person;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class BookDTO {
    Long id;
    String title;
    EntityProxy<Long> author;
    List<BookChapterDTO> chapters = new ArrayList<>();
}
