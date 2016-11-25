package org.crud.showcase.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookChapterDTO {
    Long id;
    String title;
    Integer orderNumber;
}
