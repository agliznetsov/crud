package org.crud.showcase.dto;

import lombok.Data;
import org.crud.core.annotations.Filterable;
import org.crud.core.data.EntityProxy;

import java.util.ArrayList;
import java.util.List;

@Data
public class BookDTO {
    Long id;
    String title;
    @Filterable
    EntityProxy<Long> author;
    List<BookChapterDTO> chapters = new ArrayList<>();
}
