package ru.otus.hw.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class BookDto {
    private String id;

    private String title;

    private AuthorDto authorDto;

    private List<GenreDto> listDtoGenres;

}
