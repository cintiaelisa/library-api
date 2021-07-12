package br.com.cee.libraryapi.api.dto;

import lombok.*;

@Setter
@Getter
public class BookDTO {
    private Long id;
    private String title;
    private String author;
    private String isbn;
}
