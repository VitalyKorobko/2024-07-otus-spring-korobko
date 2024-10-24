package ru.otus.hw.converters;

import org.springframework.stereotype.Component;
import ru.otus.hw.dto.GenreDto;

@Component
public class GenreDtoConverter {
    public String genreDtoToString(GenreDto genreDto) {
        return "Id: %s, Name: %s".formatted(genreDto.getId(), genreDto.getName());
    }

}
