package ru.otus.hw.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class BookMongoDto {
    private String id;

    private String title;

    private AuthorMongoDto authorMongoDto;

    private List<GenreMongoDto> genreMongoDtoList;

}
