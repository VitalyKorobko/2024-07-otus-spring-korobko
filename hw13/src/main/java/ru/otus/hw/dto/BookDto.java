package ru.otus.hw.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class BookDto {
    private long id;

    private String title;

    private AuthorDto authorDto;

    private List<GenreDto> listDtoGenres;

    public BookDto(long id) {
        this.id = id;
    }
}
