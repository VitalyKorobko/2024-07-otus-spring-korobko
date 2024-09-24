package ru.otus.hw.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class BookDto {
    private long id;

    private String title;

    private long authorId;

    private String authorName;

    private Map<Long, String> mapGenres;
}
